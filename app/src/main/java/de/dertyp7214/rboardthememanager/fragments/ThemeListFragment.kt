package de.dertyp7214.rboardthememanager.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowInsets
import android.view.WindowInsetsAnimation
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import de.dertyp7214.rboardcomponents.utils.asyncInto
import de.dertyp7214.rboardthememanager.R
import de.dertyp7214.rboardthememanager.adapter.ThemeAdapter
import de.dertyp7214.rboardthememanager.components.LayoutManager
import de.dertyp7214.rboardthememanager.components.MarginItemDecoration
import de.dertyp7214.rboardthememanager.core.applyTransitions
import de.dertyp7214.rboardthememanager.core.applyTransitionsViewCreated
import de.dertyp7214.rboardthememanager.core.dp
import de.dertyp7214.rboardthememanager.core.dpToPx
import de.dertyp7214.rboardthememanager.core.dpToPxRounded
import de.dertyp7214.rboardthememanager.core.get
import de.dertyp7214.rboardthememanager.core.getAttr
import de.dertyp7214.rboardthememanager.core.setMargin
import de.dertyp7214.rboardthememanager.data.ThemeDataClass
import de.dertyp7214.rboardthememanager.utils.ThemeUtils
import de.dertyp7214.rboardthememanager.viewmodels.MainViewModel
import java.lang.Integer.max

class ThemeListFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        applyTransitions()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_theme_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        applyTransitionsViewCreated()

        val recyclerView = view.findViewById<RecyclerView>(R.id.recycler_view)
        val refreshLayout = view.findViewById<SwipeRefreshLayout>(R.id.refreshLayout)

        val unfilteredThemeList = arrayListOf<ThemeDataClass>()
        val themeList = arrayListOf<ThemeDataClass>()

        val mainViewModel = requireActivity()[MainViewModel::class.java]

        val adapter = ThemeAdapter(requireContext(), themeList, null, { state, adapter ->
            mainViewModel.setSelections(
                Pair(state == ThemeAdapter.SelectionState.SELECTING, adapter)
            )
        }, null, mainViewModel::setSelectedTheme)

        mainViewModel.themesObserve(this) { themes ->
            if (themes.isEmpty()) {
                refreshLayout.isRefreshing = true
                ThemeUtils::loadThemes asyncInto mainViewModel::setThemes
            } else {
                synchronized(unfilteredThemeList) {
                    synchronized(themeList) {
                        unfilteredThemeList.clear()
                        unfilteredThemeList.addAll(themes)
                        themeList.clear()
                        themeList.addAll(unfilteredThemeList.filter {
                            it.name.contains(
                                mainViewModel.getFilter(),
                                true
                            )
                        })
                        adapter.notifyDataChanged()
                        refreshLayout.isRefreshing = false
                    }
                }
            }
        }

        refreshLayout.setOnApplyWindowInsetsListener { insetsView, windowInsets ->
            insetsView.setMargin(
                bottomMargin = max(
                    windowInsets.getInsets(WindowInsets.Type.systemBars() or WindowInsets.Type.ime()).bottom - 64.dp(
                        requireContext()
                    ) - windowInsets.getInsets(WindowInsets.Type.navigationBars()).bottom,
                    2.dp(requireContext())
                )
            )
            windowInsets
        }

        refreshLayout.setWindowInsetsAnimationCallback(object :
            WindowInsetsAnimation.Callback(DISPATCH_MODE_STOP) {
            override fun onProgress(
                insets: WindowInsets,
                runningAnimations: MutableList<WindowInsetsAnimation>
            ): WindowInsets {
                return insets
            }
        })
        refreshLayout.setProgressViewOffset(
            true,
            0,
            5.dpToPx(requireContext()).toInt()
        )
        refreshLayout.setProgressBackgroundColorSchemeColor(requireActivity().getAttr(com.google.android.material.R.attr.colorBackgroundFloating))
        refreshLayout.setColorSchemeColors(
            requireActivity().getAttr(com.google.android.material.R.attr.colorPrimary),
            requireActivity().getAttr(
                com.google.android.material.R.attr.colorTertiary
            )
        )

        recyclerView.layoutManager = LayoutManager(requireContext())
        recyclerView.setHasFixedSize(false)
        recyclerView.adapter = adapter
        recyclerView.addItemDecoration(MarginItemDecoration(2.1.dpToPxRounded(requireContext())))

        if (mainViewModel.getThemes().isEmpty() && mainViewModel.loaded()) {
            refreshLayout.isRefreshing = true
            ThemeUtils::loadThemes asyncInto mainViewModel::setThemes
        }

        mainViewModel.observeFilter(this) { filter ->
            synchronized(themeList) {
                themeList.clear()
                themeList.addAll(unfilteredThemeList.filter {
                    it.name.contains(filter, true)
                })
                adapter.notifyDataChanged()
            }
        }

        mainViewModel.onRefreshThemes(this) {
            adapter.notifyDataChanged()
        }

        refreshLayout.setOnRefreshListener {
            refreshLayout.isRefreshing = true
            mainViewModel.setFilter()
            mainViewModel.clearSearch()
            ThemeUtils::loadThemes asyncInto mainViewModel::setThemes
        }
    }
}