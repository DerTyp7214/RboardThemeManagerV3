package de.dertyp7214.rboardthememanager.fragments

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import de.dertyp7214.rboardthememanager.R
import de.dertyp7214.rboardthememanager.adapter.ThemePackAdapter
import de.dertyp7214.rboardthememanager.components.ChipContainer
import de.dertyp7214.rboardthememanager.components.LayoutManager
import de.dertyp7214.rboardthememanager.core.applyTransitions
import de.dertyp7214.rboardthememanager.core.applyTransitionsViewCreated
import de.dertyp7214.rboardthememanager.core.get
import de.dertyp7214.rboardthememanager.data.ThemePack
import de.dertyp7214.rboardthememanager.utils.ThemeUtils
import de.dertyp7214.rboardthememanager.utils.TraceWrapper
import de.dertyp7214.rboardthememanager.utils.asyncInto
import de.dertyp7214.rboardthememanager.utils.doAsync
import de.dertyp7214.rboardthememanager.viewmodels.MainViewModel

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

        val mainViewModel = requireActivity()[MainViewModel::class.java]

        val originalThemePacks = ArrayList(mainViewModel.getThemePacks())
        val themePacks = arrayListOf(ThemePack.NONE, *originalThemePacks.toTypedArray())

        trace.addSplit("RESULT LAUNCHER")

        val resultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                if (it.resultCode == Activity.RESULT_OK) {
                    mainViewModel.setThemes()
                    mainViewModel.navigate(R.id.navigation_themes)
                }
            }

        trace.addSplit("ADAPTER")

        val adapter = ThemePackAdapter(themePacks, requireActivity(), resultLauncher)

        val tags = arrayListOf<String>()

        trace.addSplit("OBSERVERS")

        mainViewModel.themePacksObserve(this) { packs ->
            if (packs.isEmpty()) ThemeUtils::loadThemePacks asyncInto mainViewModel::setThemePacks
            else
                doAsync({
                    originalThemePacks.clear()
                    try {
                        originalThemePacks.add(ThemePack.NONE)
                        originalThemePacks.addAll(packs)
                        themePacks.clear()
                        themePacks.addAll(originalThemePacks)
                        packs.forEach { pack ->
                            tags.addAll(pack.tags.filter { !tags.contains(it) })
                        }
                    } catch (e: Exception) {
                    }
                }) {
                    adapter.notifyDataSetChanged()
                    chipContainer.setChips(tags)
                }
        }

        mainViewModel.observeFilter(this) { filter ->
            val chipFilters = chipContainer.filters
            themePacks.clear()
            themePacks.addAll(filterThemePacks(originalThemePacks, chipFilters, filter))
            adapter.notifyDataSetChanged()
        }

        trace.addSplit("RECYCLERVIEW")

        recyclerView.layoutManager = LayoutManager(requireContext())
        recyclerView.setHasFixedSize(false)
        recyclerView.adapter = adapter

        trace.addSplit("CHIP CONTAINER")

        chipContainer.setOnFilterToggle { filters ->
            doAsync({
                val searchFilter = mainViewModel.getFilter()
                themePacks.clear()
                try {
                    themePacks.addAll(filterThemePacks(originalThemePacks, filters, searchFilter))
                } catch (e: Exception) {
                }
            }) {
                adapter.notifyDataSetChanged()
            }
        }

        trace.addSplit("LOAD THEME PACKS")

        if (mainViewModel.getThemePacks().isEmpty())
            ThemeUtils::loadThemePacks asyncInto mainViewModel::setThemePacks

        trace.end()
    }

    private fun filterThemePacks(
        packs: List<ThemePack>,
        filters: List<String>,
        filter: String
    ): List<ThemePack> {
        return packs.filter { pack ->
            pack.none ||
                    ((pack.name.contains(
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