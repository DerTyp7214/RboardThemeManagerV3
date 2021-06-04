package de.dertyp7214.rboardthememanager.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import de.dertyp7214.rboardthememanager.R
import de.dertyp7214.rboardthememanager.adapter.ThemeAdapter
import de.dertyp7214.rboardthememanager.core.dpToPx
import de.dertyp7214.rboardthememanager.core.getAttrColor
import de.dertyp7214.rboardthememanager.data.ThemeDataClass
import de.dertyp7214.rboardthememanager.utils.ThemeUtils
import de.dertyp7214.rboardthememanager.utils.asyncInto
import de.dertyp7214.rboardthememanager.viewmodels.ThemesViewModel

class ThemeListFragment : Fragment() {

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_theme_list, container, false)

        val recyclerView = view.findViewById<RecyclerView>(R.id.recycler_view)
        val refreshLayout = view.findViewById<SwipeRefreshLayout>(R.id.refreshLayout)

        val unfilteredThemeList = arrayListOf<ThemeDataClass>()
        val themeList = arrayListOf<ThemeDataClass>()

        val themesViewModel = requireActivity().run {
            ViewModelProvider(this)[ThemesViewModel::class.java]
        }

        val adapter = ThemeAdapter(requireContext(), themeList) { theme ->
            themesViewModel.setSelectedTheme(theme)
        }

        refreshLayout.isRefreshing = themesViewModel.getThemes().isEmpty()
        refreshLayout.setProgressViewOffset(
            true,
            0,
            5.dpToPx(requireContext()).toInt()
        )
        refreshLayout.setProgressBackgroundColorSchemeColor(requireActivity().getAttrColor(R.attr.colorBackgroundFloating))
        refreshLayout.setColorSchemeColors(requireActivity().getAttrColor(R.attr.colorOnPrimary))

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.setHasFixedSize(true)
        recyclerView.adapter = adapter

        themesViewModel.themesObserve(this) { themes ->
            if (themes.isEmpty()) {
                refreshLayout.isRefreshing = true
                ThemeUtils::loadThemes asyncInto themesViewModel::setThemes
            } else {
                unfilteredThemeList.clear()
                unfilteredThemeList.addAll(themes)
                themeList.clear()
                themeList.addAll(unfilteredThemeList.filter {
                    it.name.contains(
                        themesViewModel.getFilter(),
                        true
                    )
                })
                adapter.notifyDataSetChanged()
                refreshLayout.isRefreshing = false
            }
        }

        themesViewModel.observeFilter(this) { filter ->
            themeList.clear()
            themeList.addAll(unfilteredThemeList.filter {
                it.name.contains(filter, true)
            })
            adapter.notifyDataSetChanged()
        }

        refreshLayout.setOnRefreshListener {
            refreshLayout.isRefreshing = true
            themesViewModel.setFilter()
            themesViewModel.clearSearch()
            ThemeUtils::loadThemes asyncInto themesViewModel::setThemes
        }

        if (themesViewModel.getThemes().isEmpty())
            ThemeUtils::loadThemes asyncInto themesViewModel::setThemes

        return view
    }
}