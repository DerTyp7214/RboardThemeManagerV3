package de.dertyp7214.rboardthememanager.fragments

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowInsets
import android.view.WindowInsetsAnimation
import android.view.WindowInsetsAnimation.Callback.DISPATCH_MODE_STOP
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import de.dertyp7214.rboardthememanager.Config.IS_MIUI
import de.dertyp7214.rboardthememanager.R
import de.dertyp7214.rboardthememanager.adapter.ThemePackAdapter
import de.dertyp7214.rboardthememanager.components.ChipContainer
import de.dertyp7214.rboardthememanager.components.LayoutManager
import de.dertyp7214.rboardthememanager.core.applyTransitions
import de.dertyp7214.rboardthememanager.core.applyTransitionsViewCreated
import de.dertyp7214.rboardthememanager.core.get
import de.dertyp7214.rboardthememanager.core.showMaterial
import de.dertyp7214.rboardthememanager.data.ThemePack
import de.dertyp7214.rboardthememanager.screens.InstallPackActivity
import de.dertyp7214.rboardthememanager.utils.*
import de.dertyp7214.rboardcomponents.utils.asyncInto
import de.dertyp7214.rboardcomponents.utils.doAsyncCallback
import de.dertyp7214.rboardthememanager.Config
import de.dertyp7214.rboardthememanager.components.MarginItemDecoration
import de.dertyp7214.rboardthememanager.core.dp
import de.dertyp7214.rboardthememanager.core.dpToPxRounded
import de.dertyp7214.rboardthememanager.core.setMargin
import de.dertyp7214.rboardthememanager.viewmodels.MainViewModel
import java.lang.Integer.max

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

    @SuppressLint("NotifyDataSetChanged", "ShowToast")
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
                    InstallPackActivity.toast?.cancel()
                    Snackbar.make(
                        requireView(),
                        R.string.themes_installed,
                        Snackbar.LENGTH_LONG
                    )
                        .setAnchorView(requireActivity().findViewById(R.id.bottom_bar))
                        .also { snackbar ->
                            if (IS_MIUI || Config.useMagisk)
                                snackbar.setAction(
                                    R.string.reboot
                                ) {
                                    RootUtils.reboot()
                                }
                        }.showMaterial()
                }
            }

        trace.addSplit("ADAPTER")

        val adapter = ThemePackAdapter(requireContext(),themePacks, requireActivity(), resultLauncher)

        val tags = arrayListOf<String>()

        trace.addSplit("OBSERVERS")

        mainViewModel.themePacksObserve(this) { packs ->
            if (packs.isEmpty()) ThemeUtils::loadThemePacks asyncInto mainViewModel::setThemePacks
            else
                doAsyncCallback<Any?>({
                    synchronized(originalThemePacks) {
                        synchronized(themePacks) {
                            originalThemePacks.clear()
                            originalThemePacks.add(ThemePack.NONE)
                            originalThemePacks.addAll(packs)
                            themePacks.clear()
                            themePacks.addAll(originalThemePacks)
                            packs.forEach { pack ->
                                tags.addAll(pack.tags.filter { !tags.contains(it) })
                            }
                            it(null)
                        }
                    }
                }) {
                    adapter.notifyDataSetChanged()
                    chipContainer.setChips(tags)
                }
        }

        mainViewModel.observeFilter(this) { filter ->
            val chipFilters = chipContainer.filters
            val sortByDate = mainViewModel.packsSortByDate()
            val includeThemeNames = mainViewModel.includeThemeNames()
            synchronized(themePacks) {
                themePacks.clear()
                themePacks.addAll(
                    filterThemePacks(
                        originalThemePacks,
                        chipFilters,
                        filter,
                        sortByDate,
                        includeThemeNames
                    )
                )
                adapter.notifyDataSetChanged()
            }
        }

        mainViewModel.observePacksSortByDate(this) { sortByDate ->
            synchronized(themePacks) {
                themePacks.clear()
                themePacks.addAll(
                    filterThemePacks(
                        originalThemePacks,
                        chipContainer.filters,
                        mainViewModel.getFilter(),
                        sortByDate,
                        mainViewModel.includeThemeNames()
                    )
                )
                adapter.notifyDataSetChanged()
            }
        }

        mainViewModel.observeIncludeThemeNames(this) { includeThemeNames ->
            synchronized(themePacks) {
                themePacks.clear()
                themePacks.addAll(
                    filterThemePacks(
                        originalThemePacks,
                        chipContainer.filters,
                        mainViewModel.getFilter(),
                        mainViewModel.packsSortByDate(),
                        includeThemeNames
                    )
                )
                adapter.notifyDataSetChanged()
            }
        }

        trace.addSplit("RECYCLERVIEW")

        recyclerView.layoutManager = LayoutManager(requireContext())
        recyclerView.setHasFixedSize(false)
        recyclerView.adapter = adapter
        recyclerView.addItemDecoration(MarginItemDecoration(2.dpToPxRounded(requireContext())))
        recyclerView.setOnApplyWindowInsetsListener { insetsView, windowInsets ->
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                insetsView.setMargin(
                    bottomMargin = max(
                        windowInsets.getInsets(WindowInsets.Type.systemBars() or WindowInsets.Type.ime()).bottom - 90.dp(
                            requireContext()
                        ) - windowInsets.getInsets(WindowInsets.Type.navigationBars()).bottom,
                        2.dp(requireContext())
                    )
                )
            }
            windowInsets
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            recyclerView.setWindowInsetsAnimationCallback(object :
                WindowInsetsAnimation.Callback(DISPATCH_MODE_STOP) {
                override fun onProgress(
                    insets: WindowInsets,
                    runningAnimations: MutableList<WindowInsetsAnimation>
                ): WindowInsets {
                    return insets
                }
            })
        }
        trace.addSplit("CHIP CONTAINER")

        chipContainer.setOnFilterToggle { filters ->
            doAsyncCallback<Any?>({
                val searchFilter = mainViewModel.getFilter()
                val sortByDate = mainViewModel.packsSortByDate()
                val includeThemeNames = mainViewModel.includeThemeNames()
                synchronized(themePacks) {
                    themePacks.clear()
                    themePacks.addAll(
                        filterThemePacks(
                            originalThemePacks,
                            filters,
                            searchFilter,
                            sortByDate,
                            includeThemeNames
                        )
                    )
                    it(null)
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
        filter: String,
        sortByDate: Boolean,
        includeThemeNames: Boolean
    ): List<ThemePack> {
        return packs.filter { pack ->
            pack.none ||
                    ((pack.name.contains(
                        filter,
                        true
                    ) || pack.author.contains(
                        filter,
                        true
                    ) || (includeThemeNames && pack.themes?.any {
                        it.contains(
                            filter,
                            true
                        )
                    } ?: false)) && (filters.isEmpty() || pack.tags.any {
                        filters.contains(
                            it
                        )
                    }))
        }.let {
            if (sortByDate) it.sortedByDescending { item -> item.date }
            else it
        }
    }
}