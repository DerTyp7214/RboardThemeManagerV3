package de.dertyp7214.rboardthememanager.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import de.dertyp7214.rboardthememanager.R
import de.dertyp7214.rboardthememanager.adapter.ThemeAdapter
import de.dertyp7214.rboardthememanager.data.ThemeDataClass
import de.dertyp7214.rboardthememanager.viewmodels.ThemesViewModel

class ThemeListFragment : Fragment() {

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_theme_list, container, false)

        val recyclerView = view.findViewById<RecyclerView>(R.id.recycler_view)
        val cardView = view.findViewById<CardView>(R.id.cardView)

        val unfilteredThemeList = arrayListOf<ThemeDataClass>()
        val themeList = arrayListOf<ThemeDataClass>()

        val adapter = ThemeAdapter(requireContext(), themeList)

        val themesViewModel = requireActivity().run {
            ViewModelProvider(this)[ThemesViewModel::class.java]
        }

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.setHasFixedSize(true)
        recyclerView.adapter = adapter

        themesViewModel.themesObserve(this) { themes ->
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
        }

        themesViewModel.observeFilter(this) { filter ->
            themeList.clear()
            themeList.addAll(unfilteredThemeList.filter {
                it.name.contains(filter, true)
            })
            adapter.notifyDataSetChanged()
        }

        return view
    }
}