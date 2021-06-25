package de.dertyp7214.rboardthememanager.fragments

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import de.dertyp7214.rboardthememanager.R
import de.dertyp7214.rboardthememanager.adapter.ThemePackAdapter
import de.dertyp7214.rboardthememanager.components.ChipContainer
import de.dertyp7214.rboardthememanager.core.applyTransitions
import de.dertyp7214.rboardthememanager.core.applyTransitionsViewCreated
import de.dertyp7214.rboardthememanager.core.get
import de.dertyp7214.rboardthememanager.data.ThemePack
import de.dertyp7214.rboardthememanager.utils.ThemeUtils
import de.dertyp7214.rboardthememanager.utils.TraceWrapper
import de.dertyp7214.rboardthememanager.utils.asyncInto
import de.dertyp7214.rboardthememanager.utils.doAsync
import de.dertyp7214.rboardthememanager.viewmodels.ThemesViewModel

class DownloadListFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        applyTransitions()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_download_list, container, false)
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        applyTransitionsViewCreated()

        val trace = TraceWrapper("DOWNLOADS", false)
        trace.addSplit("GET VIEWS")

        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerView)
        val chipContainer = view.findViewById<ChipContainer>(R.id.chipContainer)

        val themesViewModel = requireActivity()[ThemesViewModel::class.java]

        val originalThemePacks = ArrayList(themesViewModel.getThemePacks())
        val themePacks = arrayListOf(ThemePack.NONE, *originalThemePacks.toTypedArray())

        trace.addSplit("RESULT LAUNCHER")

        val resultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                if (it.resultCode == Activity.RESULT_OK) {
                    themesViewModel.setThemes()
                    themesViewModel.navigate(R.id.navigation_themes)
                }
            }

        trace.addSplit("ADAPTER")

        val adapter = ThemePackAdapter(themePacks, requireActivity(), resultLauncher)

        val tags = arrayListOf<String>()

        trace.addSplit("OBSERVERS")

        themesViewModel.themePacksObserve(this) { packs ->
            if (packs.isEmpty()) ThemeUtils::loadThemePacks asyncInto themesViewModel::setThemePacks
            else
                doAsync({
                    originalThemePacks.clear()
                    originalThemePacks.add(ThemePack.NONE)
                    originalThemePacks.addAll(packs)
                    themePacks.clear()
                    themePacks.addAll(originalThemePacks)
                    packs.forEach { pack ->
                        tags.addAll(pack.tags.filter { !tags.contains(it) })
                    }
                }) {
                    adapter.notifyDataSetChanged()
                    chipContainer.setChips(tags)
                }
        }

        themesViewModel.observeFilter(this) { filter ->
            doAsync({
                val chipFilters = chipContainer.filters
                themePacks.clear()
                themePacks.addAll(filterThemePacks(originalThemePacks, chipFilters, filter))
            }) {
                adapter.notifyDataSetChanged()
            }
        }

        trace.addSplit("RECYCLERVIEW")

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.setHasFixedSize(false)
        recyclerView.adapter = adapter

        trace.addSplit("CHIP CONTAINER")

        chipContainer.setOnFilterToggle { filters ->
            doAsync({
                val searchFilter = themesViewModel.getFilter()
                themePacks.clear()
                themePacks.addAll(filterThemePacks(originalThemePacks, filters, searchFilter))
            }) {
                adapter.notifyDataSetChanged()
            }
        }

        trace.addSplit("LOAD THEME PACKS")

        if (themesViewModel.getThemePacks().isEmpty())
            ThemeUtils::loadThemePacks asyncInto themesViewModel::setThemePacks

        trace.end()
    }

    private fun filterThemePacks(
        packs: List<ThemePack>,
        filters: List<String>,
        filter: String
    ): List<ThemePack> {
        return packs.filter { pack ->
            pack.none ||
                    ((pack.title.contains(
                        filter,
                        true
                    ) || pack.author.contains(
                        filter,
                        true
                    )) && (filters.isEmpty() || pack.tags.any {
                        filters.contains(
                            it
                        )
                    }))
        }
    }
}