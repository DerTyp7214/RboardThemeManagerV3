@file:Suppress("DEPRECATION")

package de.dertyp7214.rboardthememanager.screens

import android.annotation.SuppressLint
import android.content.Intent
import android.content.Intent.ACTION_SEND
import android.graphics.Color
import android.graphics.RenderEffect
import android.graphics.Shader
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.core.content.edit
import androidx.core.widget.NestedScrollView
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dertyp7214.logs.helpers.Logger
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.button.MaterialButton
import com.google.android.material.snackbar.Snackbar
import com.skydoves.balloon.ArrowOrientation
import com.skydoves.balloon.BalloonAnimation
import com.skydoves.balloon.BalloonSizeSpec
import com.skydoves.balloon.createBalloon
import com.topjohnwu.superuser.io.SuFile
import de.dertyp7214.rboardcomponents.utils.asyncInto
import de.dertyp7214.rboardcomponents.utils.doAsync
import de.dertyp7214.rboardthememanager.BuildConfig
import de.dertyp7214.rboardthememanager.Config
import de.dertyp7214.rboardthememanager.Config.MODULE_META
import de.dertyp7214.rboardthememanager.Config.PATCHER_PACKAGE
import de.dertyp7214.rboardthememanager.Config.PLAY_URL
import de.dertyp7214.rboardthememanager.R
import de.dertyp7214.rboardthememanager.adapter.MenuAdapter
import de.dertyp7214.rboardthememanager.core.addCallback
import de.dertyp7214.rboardthememanager.core.applyTheme
import de.dertyp7214.rboardthememanager.core.content
import de.dertyp7214.rboardthememanager.core.delayed
import de.dertyp7214.rboardthememanager.core.delete
import de.dertyp7214.rboardthememanager.core.dpToPx
import de.dertyp7214.rboardthememanager.core.enableBlur
import de.dertyp7214.rboardthememanager.core.get
import de.dertyp7214.rboardthememanager.core.getAttr
import de.dertyp7214.rboardthememanager.core.getNavigationBarHeight
import de.dertyp7214.rboardthememanager.core.isReachable
import de.dertyp7214.rboardthememanager.core.moveToCache
import de.dertyp7214.rboardthememanager.core.notify
import de.dertyp7214.rboardthememanager.core.openDialog
import de.dertyp7214.rboardthememanager.core.openShareThemeDialog
import de.dertyp7214.rboardthememanager.core.openUrl
import de.dertyp7214.rboardthememanager.core.preferences
import de.dertyp7214.rboardthememanager.core.runAsCommand
import de.dertyp7214.rboardthememanager.core.set
import de.dertyp7214.rboardthememanager.core.setHeight
import de.dertyp7214.rboardthememanager.core.setMargin
import de.dertyp7214.rboardthememanager.core.share
import de.dertyp7214.rboardthememanager.core.showMaterial
import de.dertyp7214.rboardthememanager.core.toHumanReadableBytes
import de.dertyp7214.rboardthememanager.data.MenuItem
import de.dertyp7214.rboardthememanager.data.ThemeDataClass
import de.dertyp7214.rboardthememanager.databinding.ActivityMainBinding
import de.dertyp7214.rboardthememanager.dialogs.UsageDialog
import de.dertyp7214.rboardthememanager.preferences.Flags
import de.dertyp7214.rboardthememanager.utils.AppStartUp
import de.dertyp7214.rboardthememanager.utils.MagiskUtils
import de.dertyp7214.rboardthememanager.utils.PackageUtils
import de.dertyp7214.rboardthememanager.utils.ThemeUtils
import de.dertyp7214.rboardthememanager.utils.ThemeUtils.getSystemAutoTheme
import de.dertyp7214.rboardthememanager.utils.UpdateHelper
import de.dertyp7214.rboardthememanager.utils.ZipHelper
import de.dertyp7214.rboardthememanager.utils.applyTheme
import de.dertyp7214.rboardthememanager.viewmodels.MainViewModel
import dev.chrisbanes.insetter.applyInsetter
import java.io.File
import java.net.URL
import kotlin.math.roundToInt


class MainActivity : AppCompatActivity() {

    private val updateUrl: (versionName: String) -> String = { versionName ->
        @Suppress("KotlinConstantConditions")
        "https://github.com/DerTyp7214/RboardThemeManagerV3/releases/download/${versionName}${
            BuildConfig.BUILD_TYPE.let {
                if (it == "release") "-rCompatible" else "-rCompatible-debug"
            }
        }/app-${BuildConfig.BUILD_TYPE}.apk"
    }

    private val updateUrlGitlab by lazy {
        if (BuildConfig.DEBUG){
            "https://gitlab.com/dertyp7214/RboardMirror/-/raw/main/debug/app-rCompatible-debug.apk"
        }
        else{
            "https://gitlab.com/dertyp7214/RboardMirror/-/raw/main/release/app-rCompatible-release.apk"
        }
    }

    @Suppress("MemberVisibilityCanBePrivate")
    companion object {
        private val instances = arrayListOf<MainActivity>()

        fun clearInstances() {
            while (instances.isNotEmpty()) popInstance()
        }

        fun popInstance() {
            if (Build.VERSION.SDK_INT >= 35) {
                instances.removeLast().finish()
            } else {
                instances.removeLastOrNull()?.finish()
            }
        }
    }

    private lateinit var downloadResultLauncher: ActivityResultLauncher<Intent>
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<NestedScrollView>
    private lateinit var mainViewModel: MainViewModel

    private lateinit var binding: ActivityMainBinding

    private val searchBar by lazy { binding.searchToolbar.searchBar }

    private val callbacks: ArrayList<OnBackPressedCallback> = arrayListOf()
    @SuppressLint("NotifyDataSetChanged", "ShowToast")
    override fun onCreate(savedInstanceState: Bundle?) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            applyTheme(main = true)
        }
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.Q) {
            enableEdgeToEdge(
                statusBarStyle = SystemBarStyle.auto(Color.TRANSPARENT, Color.TRANSPARENT)
            )
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.setDecorFitsSystemWindows(false)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            window.isNavigationBarContrastEnforced = false
            window.navigationBarColor = Color.TRANSPARENT
        }
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        instances.add(this)

        mainViewModel = this[MainViewModel::class.java]

        val searchToolBar = binding.searchToolbar
        val mainContent = binding.mainContent
        val bottomSheet = findViewById<NestedScrollView>(R.id.bottom_bar)
        val navigationHolder =
            supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
        val controller = navigationHolder.navController

        val navigation = findViewById<BottomNavigationView>(R.id.navigation)
        val menuRecyclerView = findViewById<RecyclerView>(R.id.recyclerView)

        val secondaryContent = findViewById<LinearLayout>(R.id.secondaryContent)

        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)

        mainContent.foreground.alpha = 0

        searchBar.instantSearch = true
        searchToolBar.applyInsetter {
            type(statusBars = true) {
                margin()
            }
        }

        findViewById<LinearLayout>(R.id.bottomLayout).applyInsetter {
            type(navigationBars = true) {
                margin()
            }
        }

        searchBar.menuVisible = !preferences.getBoolean("demo_shown", false)

        navigation.setHeight((resources.getDimension(R.dimen.bottomBarHeight) + getNavigationBarHeight()).roundToInt())
        navigation.itemPaddingBottom = 8.dpToPx(this).toInt()

        val reloadThemesLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                mainViewModel.setThemes()
                mainViewModel.setThemePacks()
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            }

        val closeBottomSheetBehaviorLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            }

        downloadResultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    content.setRenderEffect(null)
                }
            }

        callbacks.forEach { it.remove() }
        onBackPressedDispatcher.addCallback(this, true, callbacks::add) {
            when {
                bottomSheetBehavior.state == BottomSheetBehavior.STATE_EXPANDED ->
                    bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
                mainViewModel.getSelections().first -> mainViewModel.getSelections().second?.clearSelection()
                searchBar.focus -> searchBar.clearText()
                else -> {
                    isEnabled = false
                    onBackPressedDispatcher.onBackPressed()
                    isEnabled = true
                }
            }
        }

        AppStartUp(this).apply {
            setUp()
            onCreate { intent ->
                checkModuleAndUpdate(intent)

                mainViewModel.observeLoaded(this) {
                    navigate(controller, R.id.action_placeholder_to_themeListFragment)
                    ThemeUtils::loadThemes::asyncInto.delayed(200, mainViewModel::setThemes)
                }
                mainViewModel.setLoaded(true)

                searchBar.setOnMenuListener {
                    preferences.edit { putBoolean("demo_shown", true) }
                    searchBar.menuVisible = false
                    createBalloon(this) {
                        setWidth(BalloonSizeSpec.WRAP)
                        setHeight(BalloonSizeSpec.WRAP)
                        setPadding(10)
                        setMargin(8)
                        setArrowPosition(.15f)
                        setArrowOrientation(ArrowOrientation.BOTTOM)
                        setCornerRadius(resources.getDimension(R.dimen.roundCornersInner))
                        setText(getString(R.string.menu_moved))
                        setTextColor(getAttr(com.google.android.material.R.attr.colorBackgroundFloating))
                        setTextSize(12f)
                        setBackgroundColor(getAttr(com.google.android.material.R.attr.colorPrimary))
                        setBalloonAnimation(BalloonAnimation.FADE)
                        setDismissWhenClicked(true)
                        setOnBalloonDismissListener {
                            bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
                        }
                        setLifecycleOwner(lifecycleOwner)
                    }.showAlignBottom(it)
                }

                val mainMenuItems = arrayListOf(
                    MenuItem(
                        R.drawable.ic_about,
                        R.string.about
                    ) {
                        PreferencesActivity::class.java[
                                this,
                                closeBottomSheetBehaviorLauncher
                        ] = {
                            putExtra("type", "about")
                        }
                    },
                    MenuItem(
                        R.drawable.ic_settings,
                        R.string.settings
                    ) {
                        PreferencesActivity::class.java[this, reloadThemesLauncher] = {
                            putExtra("type", "settings")
                        }
                    },
                    MenuItem(
                        R.drawable.ic_baseline_outlined_flag_24,
                        R.string.flags
                    ) {
                        PreferencesActivity::class.java[
                                this,
                                closeBottomSheetBehaviorLauncher
                        ] = {
                            putExtra("type", "flags")
                        }
                    }
                )

                val menuItems = ArrayList(mainMenuItems)

                val menuAdapter = MenuAdapter(menuItems, this)

                binding.fragmentContainerView.setMargin(
                    bottomMargin = resources.getDimension(R.dimen.bottomBarHeight)
                        .toInt() + 28.dpToPx(this)
                        .toInt() + getNavigationBarHeight()
                )

                bottomSheetBehavior.isFitToContents = true
                bottomSheetBehavior.skipCollapsed = true
                bottomSheetBehavior.peekHeight =
                    resources.getDimension(R.dimen.bottomBarHeight).toInt() + 18.dpToPx(this)
                        .toInt() + getNavigationBarHeight()
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED

                bottomSheetBehavior.addBottomSheetCallback(object :
                    BottomSheetBehavior.BottomSheetCallback() {
                    override fun onStateChanged(bottomSheet: View, newState: Int) {
                        if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
                            if (mainViewModel.getSelectedTheme() != null)
                                mainViewModel.setSelectedTheme()
                        }
                    }

                    override fun onSlide(bottomSheet: View, slideOffset: Float) {
                        navigation.alpha = 1 - slideOffset
                        secondaryContent.alpha = slideOffset

                        if (navigation.alpha == 0F) navigation.visibility = GONE
                        else if (navigation.visibility == GONE) navigation.visibility = VISIBLE

                        mainContent.foreground.alpha = (slideOffset * 255).toInt()
                    }
                })

                searchToolBar.navigationIcon =
                    ContextCompat.getDrawable(this, R.drawable.ic_baseline_arrow_back_24)
                searchToolBar.setNavigationOnClickListener { mainViewModel.getSelections().second?.clearSelection() }

                searchToolBar.setOnMenuItemClickListener { item ->
                    when (item.itemId) {
                        R.id.share -> {
                            val adapter = mainViewModel.getSelections().second
                            val themes = adapter?.getSelected()?.filter {
                                it.path.isNotEmpty() && !it.path.startsWith("assets:") && !it.path.startsWith(
                                    "system_auto:"
                                ) && !it.path.startsWith("silk:") && !it.path.startsWith("rboard:")
                            }
                            if (!themes.isNullOrEmpty()) {
                                openShareThemeDialog { dialog, name, author ->
                                    val files = arrayListOf<File>()
                                    File(
                                        // Remove the Android Version check if old Android Versions are no longer supported on the Gboard side.
                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                                            cacheDir
                                        } else {
                                            externalCacheDir
                                        }, "pack.meta"
                                    ).apply {
                                        files.add(this)
                                        writeText("name=$name\nauthor=$author\n")
                                    }
                                    themes.map { it.moveToCache(this) }.forEach {
                                        val image = File(it.path.removeSuffix(".zip"))
                                        files.add(File(it.path))
                                        if (image.exists()) files.add(image)
                                    }
                                    val zip = File(
                                        // Remove the Android Version check if old Android Versions are no longer supported on the Gboard side.
                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                                            cacheDir
                                        } else {
                                            externalCacheDir
                                        }, "themes.pack"
                                    )
                                    zip.delete()
                                    ZipHelper().zip(files.map { it.absolutePath }, zip.absolutePath)
                                    files.forEach { it.delete() }
                                    zip.share(
                                        this,
                                        "application/pack",
                                        ACTION_SEND,
                                        R.string.share_themes
                                    )
                                    dialog.dismiss()
                                    adapter.clearSelection()
                                }
                            } else if (themes?.isEmpty() == true) {
                                Toast.makeText(
                                    this,
                                    R.string.select_at_least_one_not_default_theme,
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        }
                        R.id.delete -> {
                            openDialog(
                                R.string.do_you_want_to_delete_theme,
                                R.string.delete_theme
                            ) { dialog ->
                                dialog.dismiss()
                                val adapter = mainViewModel.getSelections().second
                                if (adapter != null) {
                                    val themes = adapter.getSelected().filter {
                                        it.path.isNotEmpty() && !it.path.startsWith("assets:") && !it.path.startsWith(
                                            "system_auto:") && !it.path.startsWith("silk:") && !it.path.startsWith("rboard:"
                                        )
                                    }
                                    if (themes.isNotEmpty()) {
                                        themes.forEach { theme ->
                                            theme.delete()
                                        }
                                        mainViewModel.setThemes()
                                        adapter.clearSelection()
                                    } else {
                                        Toast.makeText(
                                            this,
                                            R.string.select_at_least_one_not_default_theme,
                                            Toast.LENGTH_LONG
                                        ).show()
                                    }
                                }
                            }
                        }
                        R.id.select_all -> {
                            if (mainViewModel.getSelections().second?.getSelected()?.size == mainViewModel.getThemes().size ) {
                                mainViewModel.getSelections().second?.clearSelection()
                                item.setIcon(R.drawable.ic_select_all)
                            } else {
                                mainViewModel.getSelections().second?.selectAll()
                                item.setIcon(R.drawable.ic_deselect_all)
                            }
                        }
                    }
                    true
                }

                mainViewModel.observeSelections(this) { selections ->
                    searchToolBar.searchOpen = !selections.first
                }

                mainViewModel.observerSelectedTheme(this) { theme ->
                    if (theme?.path?.startsWith("rboard:") == true) {
                        mainViewModel.navigate(R.id.navigation_downloads)
                    } else {
                        secondaryContent.let {
                            fun removeCurrentThemeView() {
                                val view = it.findViewById<View>(R.id.current_theme_view)
                                if (view != null) it.removeView(view).also {
                                    Logger.log(Logger.Companion.Type.DEBUG, "REMOVE VIEW", view.id)
                                    removeCurrentThemeView()
                                }
                            }
                            removeCurrentThemeView()
                        }
                        if (theme != null) {
                            secondaryContent.addView(ThemeUtils.getThemeView(theme, this), 0)
                            bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
                            searchBar.clearFocus()
                            menuItems.clear()
                            if (!theme.path.startsWith("rboard:")) {
                                menuItems.add(
                                    MenuItem(
                                        R.drawable.ic_apply_theme,
                                        R.string.apply_theme
                                    ) {
                                        bottomSheetBehavior.state =
                                            BottomSheetBehavior.STATE_COLLAPSED
                                        if (applyTheme(theme, true)) {
                                            Snackbar.make(
                                                binding.fragmentContainerView,
                                                R.string.theme_applied,
                                                Snackbar.LENGTH_LONG
                                            )
                                                .setAnchorView(bottomSheet)
                                                .showMaterial()
                                        } else Toast.makeText(
                                            this,
                                            R.string.error,
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        delayed(150) {
                                            mainViewModel.setSelectedTheme()
                                            mainViewModel.refreshThemes()
                                        }
                                    })
                                fun applyTheme(dark: Boolean) {
                                    val files = mapOf(
                                        Pair(
                                            "system.prop",
                                            "ro.com.google.ime.${if (dark) "d_" else ""}theme_file=${
                                                File(
                                                    theme.fileName
                                                ).name
                                            }"
                                        )
                                    )
                                    MagiskUtils.updateModule(MODULE_META, files)
                                    "resetprop ro.com.google.ime.${if (dark) "d_" else ""}theme_file ${
                                        File(
                                            theme.fileName
                                        ).name
                                    }".runAsCommand()
                                    if (dark) Config.darkTheme = File(theme.fileName).name
                                    else Config.lightTheme = File(theme.fileName).name
                                    Flags.run {
                                        if (flagValues["oem_dark_theme"] != true) {
                                            setUpFlags()
                                            setValue(true, "oem_dark_theme", Flags.FILES.FLAGS)
                                            applyChanges()
                                        }
                                    }
                                    applyTheme(getSystemAutoTheme(), true)
                                }
                                if (!theme.path.startsWith("silk:") && !theme.path.startsWith("assets:") && !theme.path.startsWith(
                                        "system_auto:"
                                    )
                                ) {
                                    if (Config.useMagisk)
                                        menuItems.add(
                                            MenuItem(
                                                R.drawable.ic_auto_theme,
                                                R.string.apply_automatic_theme,
                                                Build.VERSION.SDK_INT > Build.VERSION_CODES.P
                                            ) {
                                                bottomSheetBehavior.state =
                                                    BottomSheetBehavior.STATE_COLLAPSED
                                                delayed(150) {
                                                    mainViewModel.setSelectedTheme()
                                                    mainViewModel.refreshThemes()
                                                }
                                                openDialog(
                                                    R.layout.auto_theme_select,
                                                    true
                                                ) { dialog ->
                                                    findViewById<TextView>(R.id.dark_theme)?.setOnClickListener {
                                                        applyTheme(true)
                                                        dialog.dismiss()
                                                    }
                                                    findViewById<TextView>(R.id.light_theme)?.setOnClickListener {
                                                        applyTheme(false)
                                                        dialog.dismiss()
                                                    }
                                                    findViewById<MaterialButton>(R.id.cancel)?.setOnClickListener { dialog.dismiss() }
                                                    findViewById<MaterialButton>(R.id.ok)?.setOnClickListener { dialog.dismiss() }
                                                }
                                            })
                                    menuItems.add(
                                        MenuItem(
                                            R.drawable.ic_patch,
                                            R.string.patch
                                        ) {
                                            val patcherPackage =
                                                if (PackageUtils.isPackageInstalled(
                                                        PATCHER_PACKAGE, packageManager
                                                    )
                                                ) PATCHER_PACKAGE
                                                else if (PackageUtils.isPackageInstalled(
                                                        "$PATCHER_PACKAGE.debug",
                                                        packageManager
                                                    )
                                                ) "$PATCHER_PACKAGE.debug"
                                                else null
                                            if (patcherPackage != null) {
                                                val themeFile = File(theme.path)
                                                val imageFile = theme.image?.let {
                                                    File(
                                                        theme.path.removeSuffix(".zip")
                                                    )
                                                }

                                                val files = arrayListOf(themeFile)
                                                if (imageFile != null) files.add(imageFile)

                                                val pack = File(
                                                    // Remove the Android Version check if old Android Versions are no longer supported on the Gboard side.
                                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                                                        cacheDir
                                                    } else {
                                                        externalCacheDir
                                                    }, "export.pack"
                                                )
                                                ZipHelper().zip(
                                                    files.map { it.absolutePath },
                                                    pack.absolutePath
                                                )
                                                ThemeUtils.shareTheme(
                                                    this,
                                                    pack,
                                                    true,
                                                    patcherPackage
                                                )
                                            } else openUrl(PLAY_URL(PATCHER_PACKAGE))
                                        }
                                    )
                                    menuItems.add(
                                        MenuItem(
                                            R.drawable.ic_delete,
                                            R.string.delete_theme
                                        ) {
                                            openDialog(
                                                R.string.q_delete_theme,
                                                R.string.delete_theme
                                            ) {
                                                bottomSheetBehavior.state =
                                                    BottomSheetBehavior.STATE_COLLAPSED
                                                if (SuFile(theme.path).delete())
                                                    Toast.makeText(
                                                        this,
                                                        R.string.theme_deleted,
                                                        Toast.LENGTH_SHORT
                                                    )
                                                        .show()
                                                else Toast.makeText(
                                                    this,
                                                    R.string.error,
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                                delayed(150) {
                                                    mainViewModel.setSelectedTheme()
                                                    mainViewModel.setThemes(listOf())
                                                }
                                            }
                                        })
                                }
                            }
                        } else {
                            secondaryContent.addView(
                                ThemeUtils.getThemeView(
                                    ThemeUtils.getActiveThemeData(),
                                    this
                                ), 0
                            )
                            menuItems.clear()
                            menuItems.addAll(mainMenuItems)
                        }
                        menuAdapter.notifyDataSetChanged()
                    }
                }

                secondaryContent.addView(
                    ThemeUtils.getThemeView(
                        ThemeUtils.getActiveThemeData(),
                        this
                    ), 0
                )

                menuRecyclerView.layoutManager = LinearLayoutManager(this)
                menuRecyclerView.setHasFixedSize(false)
                menuRecyclerView.adapter = menuAdapter

                searchBar.setOnSearchListener { text ->
                    mainViewModel.setFilter(text)
                }

                searchBar.setOnCloseListener {
                    mainViewModel.setFilter()
                }

                mainViewModel.onClearSearch(this) {
                    searchBar.clearText()
                }

                controller.addOnDestinationChangedListener { _, destination, _ ->
                    mainViewModel.setFilter()
                    mainViewModel.clearSearch()

                    when (destination.id) {
                        R.id.action_themeListFragment_to_downloadListFragment -> {
                            searchBar.setMenu(R.menu.menu_downloads)
                        }
                        R.id.action_downloadListFragment_to_themeListFragment -> {
                        }
                    }
                }

                navigation.setOnNavigationItemSelectedListener {
                    navigate(controller, it.itemId)
                    true
                }

                mainViewModel.onNavigate(this) { id ->
                    navigate(controller, id)
                    navigation.selectedItemId = id
                }
            }
        }
    }

    private fun navigate(controller: NavController, id: Int) {
        val currentDestination = controller.currentDestination?.id ?: -1

        searchBar.setMenu()

        when (id) {
            R.id.navigation_themes -> {
                if (currentDestination == R.id.downloadListFragment) {
                    controller.navigate(R.id.action_downloadListFragment_to_themeListFragment)
                } else if (currentDestination == R.id.soundsFragment) {
                    controller.navigate(R.id.action_soundsFragment_to_themeListFragment)
                }
            }
            R.id.navigation_downloads -> {
                searchBar.setMenu(R.menu.menu_downloads) {
                    when (it.itemId) {
                        R.id.sort_by_date -> {
                            it.isChecked = !it.isChecked
                            mainViewModel.setPacksSortByDate(it.isChecked)
                            true
                        }
                        R.id.include_theme_names -> {
                            it.isChecked = !it.isChecked
                            mainViewModel.setIncludeThemeNames(it.isChecked)
                            true
                        }
                        else -> false
                    }
                }
                if (currentDestination == R.id.themeListFragment) {
                    controller.navigate(R.id.action_themeListFragment_to_downloadListFragment)
                } else if (currentDestination == R.id.soundsFragment) {
                    controller.navigate(R.id.action_soundsFragment_to_downloadListFragment)
                }
            }
            R.id.navigation_sounds -> {
                if (currentDestination == R.id.themeListFragment) {
                    controller.navigate(R.id.action_themeListFragment_to_soundsFragment)
                } else if (currentDestination == R.id.downloadListFragment) {
                    controller.navigate(R.id.action_downloadListFragment_to_soundsFragment)
                }
            }
        }
    }

    private fun checkModuleAndUpdate(intent: Intent) {
        if (ThemeUtils.checkForExistingThemes()) ThemeUtils.getThemesPathFromProps()
            ?.apply { Config.THEME_LOCATION = this }

        val preferenceManager = PreferenceManager.getDefaultSharedPreferences(this)

        if (!preferenceManager.getBoolean("usageSet", false)) {
            UsageDialog.open(this, onGboard = {
                it.dismiss()
                preferenceManager.edit {
                    putBoolean("useMagisk", false)
                    putBoolean("usageSet", true)
                }
                Config.useMagisk = false
                ThemeUtils::loadThemes asyncInto mainViewModel::setThemes
            }, onMagisk = { dialogFragment ->
                dialogFragment.dismiss()
                preferenceManager.edit {
                    putBoolean("useMagisk", true)
                    putBoolean("usageSet", true)
                }
                Config.useMagisk = true
                ThemeUtils::loadThemes asyncInto mainViewModel::setThemes
                if (ThemeUtils.checkForExistingThemes()) openDialog(
                    R.string.install_module,
                    R.string.module
                ) {
                    it.dismiss()
                    MagiskUtils.installModule(this)
                } else MagiskUtils.installModule(this)
            })
        } else if (intent.getBooleanExtra(
                "update",
                this@MainActivity.intent.getBooleanExtra("update", false)
            )
        ) {
            openDialog(
                R.string.update_ready,
                R.string.update
            ) { update(intent.getStringExtra("versionName") ?: "3.7.3") }
        }
    }

    private fun update(versionName: String) {
        val maxProgress = 100
        val notificationId = 42069
        val builder =
            NotificationCompat.Builder(this, getString(R.string.download_notification_channel_id))
                .apply {
                    setContentTitle(getString(R.string.update))
                    setContentText(getString(R.string.download_update))
                    setSmallIcon(R.drawable.ic_baseline_get_app_24)
                    priority = NotificationCompat.PRIORITY_LOW
                }
        val manager = NotificationManagerCompat.from(this).apply {
            builder.setProgress(maxProgress, 0, false)
            notify(this, notificationId, builder.build())
        }
        var finished = false
        doAsync({
            if (URL(Config.GITHUB_REPO_PREFIX).isReachable()) updateUrl(versionName) else updateUrlGitlab
        }) { url ->
            UpdateHelper(url, this).apply {
                addOnProgressListener { progress, bytes, total ->
                    if (!finished) {
                        builder
                            .setContentText(
                                getString(
                                    R.string.download_update_progress,
                                    "${bytes.toHumanReadableBytes(this@MainActivity)}/${
                                        total.toHumanReadableBytes(this@MainActivity)
                                    }"
                                )
                            )
                            .setProgress(maxProgress, progress.toInt(), false)
                        notify(manager, notificationId, builder.build())
                    }
                }
                setFinishListener { path, _ ->
                    finished = true
                    manager.cancel(notificationId)
                    if (enableBlur) if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                        content.setRenderEffect(
                            RenderEffect.createBlurEffect(
                                10F,
                                10F,
                                Shader.TileMode.REPEAT
                            )
                        )
                    }
                    PackageUtils.install(this@MainActivity, File(path), downloadResultLauncher) {
                        if (enableBlur) if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                            content.setRenderEffect(null)
                        }
                        Toast.makeText(this@MainActivity, R.string.error, Toast.LENGTH_LONG).show()
                    }
                }
                setErrorListener {
                    finished = true
                    builder.setContentText(getString(R.string.download_error))
                        .setProgress(0, 0, false)
                    notify(manager, notificationId, builder.build())
                    it?.connectionException?.printStackTrace()
                    Log.d("ERROR", it?.serverErrorMessage ?: "NOO")
                }
            }.start()
        }
    }

    override fun onDestroy() {
        callbacks.forEach { it.remove() }
        instances.remove(this)
        super.onDestroy()
    }
}
