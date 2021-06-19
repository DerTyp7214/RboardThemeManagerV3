package de.dertyp7214.rboardthememanager.fragments

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
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
import de.dertyp7214.rboardthememanager.components.NewsCards
import de.dertyp7214.rboardthememanager.core.download
import de.dertyp7214.rboardthememanager.core.get
import de.dertyp7214.rboardthememanager.data.ThemePack
import de.dertyp7214.rboardthememanager.screens.InstallPackActivity
import de.dertyp7214.rboardthememanager.utils.ThemeUtils
import de.dertyp7214.rboardthememanager.utils.asyncInto
import de.dertyp7214.rboardthememanager.viewmodels.ThemesViewModel

class DownloadListFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_download_list, container, false)
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerView)
        val chipContainer = view.findViewById<ChipContainer>(R.id.chipContainer)
        val newsCards = view.findViewById<NewsCards>(R.id.newsCard)

        val originalThemePacks = arrayListOf<ThemePack>()
        val themePacks = ArrayList(originalThemePacks)

        val themesViewModel = requireActivity()[ThemesViewModel::class.java]

        val resultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                if (it.resultCode == Activity.RESULT_OK) {
                    themesViewModel.setThemes()
                    themesViewModel.navigate(R.id.navigation_themes)
                }
            }

        val adapter = ThemePackAdapter(themePacks, requireActivity(), resultLauncher)

        val tags = arrayListOf<String>()

        newsCards.setClickNewsListener { pack ->
            pack.download(requireActivity()) {
                resultLauncher.launch(
                    Intent(
                        activity,
                        InstallPackActivity::class.java
                    ).putStringArrayListExtra("themes", ArrayList(it))
                )
            }
        }

        themesViewModel.themePacksObserve(this) { packs ->
            originalThemePacks.clear()
            originalThemePacks.addAll(packs)
            themePacks.clear()
            themePacks.addAll(originalThemePacks)
            packs.forEach { pack ->
                tags.addAll(pack.tags.filter { !tags.contains(it) })
            }

            adapter.notifyDataSetChanged()
            chipContainer.setChips(tags)
        }

        themesViewModel.observeFilter(this) { filter ->
            val chipFilters = chipContainer.filters
            themePacks.clear()
            themePacks.addAll(filterThemePacks(originalThemePacks, chipFilters, filter))
            adapter.notifyDataSetChanged()
        }

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.setHasFixedSize(true)
        recyclerView.adapter = adapter

        chipContainer.setOnFilterToggle { filters ->
            val searchFilter = themesViewModel.getFilter()
            themePacks.clear()
            themePacks.addAll(filterThemePacks(originalThemePacks, filters, searchFilter))
            adapter.notifyDataSetChanged()
        }

        ThemeUtils::loadThemePacks asyncInto themesViewModel::setThemePacks
    }

    private fun filterThemePacks(
        packs: List<ThemePack>,
        filters: List<String>,
        filter: String
    ): List<ThemePack> {
        return packs.filter { pack ->
            (pack.title.contains(
                filter,
                true
            ) || pack.author.contains(
                filter,
                true
            )) && (filters.isEmpty() || pack.tags.any {
                filters.contains(
                    it
                )
            })
        }
    }
}