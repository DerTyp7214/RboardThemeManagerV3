package de.dertyp7214.rboardthememanager.fragments

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.dertyp7214.preferencesplus.core.dp
import de.dertyp7214.rboardthememanager.R
import de.dertyp7214.rboardthememanager.adapter.ThemeAdapter
import de.dertyp7214.rboardthememanager.core.*
import de.dertyp7214.rboardthememanager.data.ThemeDataClass
import de.dertyp7214.rboardthememanager.utils.ThemeUtils
import de.dertyp7214.rboardthememanager.utils.asyncInto
import de.dertyp7214.rboardthememanager.viewmodels.ThemesViewModel
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

        val themesViewModel = requireActivity()[ThemesViewModel::class.java]

        val adapter = ThemeAdapter(requireContext(), themeList, null, { state, adapter ->
            themesViewModel.setSelections(
                Pair(state == ThemeAdapter.SelectionState.SELECTING, adapter)
            )
        }, themesViewModel::setSelectedTheme)

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
                adapter.notifyDataChanged()
                refreshLayout.isRefreshing = false
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
        refreshLayout.setProgressBackgroundColorSchemeColor(requireActivity().getAttr(R.attr.colorBackgroundFloating))
        refreshLayout.setColorSchemeColors(requireActivity().getAttr(R.attr.colorOnPrimary))

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.setHasFixedSize(false)
        recyclerView.adapter = adapter

        if (themesViewModel.getThemes().isEmpty()) {
            refreshLayout.isRefreshing = true
            ThemeUtils::loadThemes asyncInto themesViewModel::setThemes
        }

        themesViewModel.observeFilter(this) { filter ->
            themeList.clear()
            themeList.addAll(unfilteredThemeList.filter {
                it.name.contains(filter, true)
            })
            adapter.notifyDataChanged()
        }

        themesViewModel.onRefreshThemes(this) {
            adapter.notifyDataChanged()
        }

        refreshLayout.setOnRefreshListener {
            refreshLayout.isRefreshing = true
            themesViewModel.setFilter()
            themesViewModel.clearSearch()
            ThemeUtils::loadThemes asyncInto themesViewModel::setThemes
        }
    }
}