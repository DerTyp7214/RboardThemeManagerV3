@file:Suppress("DEPRECATION")

package de.dertyp7214.rboardthememanager.screens

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Intent
import android.content.Intent.*
import android.graphics.RenderEffect
import android.graphics.Shader
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.Window
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.animation.doOnEnd
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.core.content.edit
import androidx.core.view.WindowCompat
import androidx.core.view.marginBottom
import androidx.core.widget.NestedScrollView
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dertyp7214.logs.helpers.Logger
import com.dertyp7214.preferencesplus.core.dp
import com.dertyp7214.preferencesplus.core.setHeight
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.button.MaterialButton
import com.topjohnwu.superuser.io.SuFile
import de.dertyp7214.rboardthememanager.Config
import de.dertyp7214.rboardthememanager.Config.MODULE_META
import de.dertyp7214.rboardthememanager.R
import de.dertyp7214.rboardthememanager.adapter.MenuAdapter
import de.dertyp7214.rboardthememanager.core.*
import de.dertyp7214.rboardthememanager.data.MenuItem
import de.dertyp7214.rboardthememanager.databinding.ActivityMainBinding
import de.dertyp7214.rboardthememanager.preferences.Flags
import de.dertyp7214.rboardthememanager.utils.*
import de.dertyp7214.rboardthememanager.utils.PackageUtils.isPackageInstalled
import de.dertyp7214.rboardthememanager.utils.ThemeUtils.getSystemAutoTheme
import de.dertyp7214.rboardthememanager.viewmodels.MainViewModel
import dev.chrisbanes.insetter.applyInsetter
import java.io.File
import kotlin.math.roundToInt

class MainActivity : AppCompatActivity() {

    private val updateUrl by lazy {
        "https://github.com/DerTyp7214/RboardThemeManagerV3/releases/download/latest-rCompatible/app-release.apk"
    }

    private lateinit var downloadResultLauncher: ActivityResultLauncher<Intent>
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<NestedScrollView>
    private lateinit var mainViewModel: MainViewModel

    private lateinit var binding: ActivityMainBinding

    @SuppressLint("NotifyDataSetChanged")
    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.setDecorFitsSystemWindows(false)
        }

        mainViewModel = this[MainViewModel::class.java]

        val toolbar = binding.toolbar
        val searchBar = binding.searchBar
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
        searchBar.applyInsetter {
            type(statusBars = true) {
                margin()
            }
        }

        findViewById<LinearLayout>(R.id.bottomLayout).applyInsetter {
            type(navigationBars = true) {
                margin()
            }
        }

        navigation.setHeight((resources.getDimension(R.dimen.bottomBarHeight) + getNavigationBarHeight()).roundToInt())

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

        AppStartUp(this).apply {
            setUp()
            onCreate { intent ->
                checkModuleAndUpdate(intent)

                mainViewModel.observeLoaded(this) {
                    navigate(controller, R.id.action_placeholder_to_themeListFragment)
                    delayed(200) {
                        ThemeUtils::loadThemes asyncInto mainViewModel::setThemes
                    }
                }
                mainViewModel.setLoaded(true)

                val mainMenuItems = arrayListOf(
                    MenuItem(
                        R.drawable.ic_info,
                        R.string.info
                    ) {
                        PreferencesActivity::class.java.start(
                            this,
                            closeBottomSheetBehaviorLauncher
                        ) {
                            putExtra("type", "info")
                        }
                    },
                    MenuItem(
                        R.drawable.ic_settings,
                        R.string.settings
                    ) {
                        PreferencesActivity::class.java.start(this, reloadThemesLauncher) {
                            putExtra("type", "settings")
                        }
                    },
                    MenuItem(
                        R.drawable.ic_baseline_outlined_flag_24,
                        R.string.flags,
                        Build.VERSION.SDK_INT > Build.VERSION_CODES.O
                    ) {
                        PreferencesActivity::class.java.start(
                            this,
                            closeBottomSheetBehaviorLauncher
                        ) {
                            putExtra("type", "flags")
                        }
                    },
                    MenuItem(
                        R.drawable.ic_creator,
                        R.string.rboard_theme_creator
                    ) {
                        if (isPackageInstalled(
                                Config.RBOARD_THEME_CREATOR_PACKAGE_NAME,
                                packageManager
                            )
                        )
                            startActivity(packageManager.getLaunchIntentForPackage(Config.RBOARD_THEME_CREATOR_PACKAGE_NAME))
                        else startActivity(
                            Intent(
                                ACTION_VIEW,
                                Uri.parse("https://github.com/DerTyp7214/RboardThemeCreator#readme")
                            )
                        )
                    }
                )

                val menuItems = ArrayList(mainMenuItems)

                val menuAdapter = MenuAdapter(menuItems, this)

                binding.fragmentContainerView.setMargin(
                    bottomMargin = resources.getDimension(R.dimen.bottomBarHeight)
                        .toInt() + 18.dpToPx(this)
                        .toInt() + getNavigationBarHeight()
                )

                bottomSheetBehavior.isFitToContents = true
                bottomSheetBehavior.skipCollapsed = true
                bottomSheetBehavior.peekHeight =
                    resources.getDimension(R.dimen.bottomBarHeight).toInt() + 8.dpToPx(this)
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

                toolbar.navigationIcon =
                    ContextCompat.getDrawable(this, R.drawable.ic_baseline_arrow_back_24)
                toolbar.setNavigationOnClickListener { mainViewModel.getSelections().second?.clearSelection() }

                toolbar.setOnMenuItemClickListener { item ->
                    when (item.itemId) {
                        R.id.share -> {
                            val adapter = mainViewModel.getSelections().second
                            val themes = adapter?.getSelected()?.filter {
                                it.path.isNotEmpty() && !it.path.startsWith("assets:") && !it.path.startsWith(
                                    "system_auto:"
                                ) && !it.path.startsWith("rboard:")
                            }
                            if (!themes.isNullOrEmpty()) {
                                openShareThemeDialog { dialog, name, author ->
                                    val files = arrayListOf<File>()
                                    File(cacheDir, "pack.meta").apply {
                                        files.add(this)
                                        writeText("name=$name\nauthor=$author\n")
                                    }
                                    themes.map { it.moveToCache(this) }.forEach {
                                        val image = File(it.path.removeSuffix(".zip"))
                                        files.add(File(it.path))
                                        if (image.exists()) files.add(image)
                                    }
                                    val zip = File(cacheDir, "themes.pack")
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
                                            "rboard:"
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
                            mainViewModel.getSelections().second?.selectAll()
                        }
                    }
                    true
                }

                mainViewModel.observeSelections(this) { selections ->
                    val originHeight = toolbar.marginBottom
                    val destinationHeight = if (selections.first) 8.dp(this) else 62.dp(this)
                    if (selections.first) toolbar.elevation = 5.dpToPx(this@MainActivity)
                    ValueAnimator.ofInt(originHeight, destinationHeight).apply {
                        addUpdateListener {
                            toolbar.setMargin(bottomMargin = it.animatedValue as Int)
                        }
                        duration = 150
                        doOnEnd {
                            if (!selections.first) toolbar.elevation = 0F
                        }
                        start()
                    }
                }

                mainViewModel.observerSelectedTheme(this) { theme ->
                    if (theme?.path?.startsWith("rboard:") == true) {
                        mainViewModel.navigate(R.id.navigation_downloads)
                    } else {
                        secondaryContent.let {
                            val view = it.findViewById<View>(R.id.current_theme_view)
                            if (view != null) it.removeView(view).also {
                                Logger.log(Logger.Companion.Type.DEBUG, "REMOVE VIEW", view.id)
                            }
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
                                        if (applyTheme(theme, true))
                                            Toast.makeText(
                                                this,
                                                R.string.applied,
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
                                if (theme.path.isNotEmpty() && !theme.path.startsWith("assets:") && !theme.path.startsWith(
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

        binding.searchBar.setMenu()

        when (id) {
            R.id.navigation_themes -> {
                if (currentDestination == R.id.downloadListFragment) {
                    controller.navigate(R.id.action_downloadListFragment_to_themeListFragment)
                } else if (currentDestination == R.id.soundsFragment) {
                    controller.navigate(R.id.action_soundsFragment_to_themeListFragment)
                }
            }
            R.id.navigation_downloads -> {
                binding.searchBar.setMenu(R.menu.menu_downloads) {
                    when (it.itemId) {
                        R.id.downloads_filter_settings -> {
                            TODO()
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

    override fun onBackPressed() {
        val searchBar = binding.searchBar
        when {
            bottomSheetBehavior.state == BottomSheetBehavior.STATE_EXPANDED ->
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            mainViewModel.getSelections().first -> mainViewModel.getSelections().second?.clearSelection()
            searchBar.focus -> searchBar.clearText()
            else -> super.onBackPressed()
        }
    }

    private fun checkModuleAndUpdate(intent: Intent) {
        if (ThemeUtils.checkForExistingThemes()) ThemeUtils.getThemesPathFromProps()
            ?.apply { Config.THEME_LOCATION = this }

        val preferenceManager = PreferenceManager.getDefaultSharedPreferences(this)

        if (!preferenceManager.getBoolean("usageSet", false)
        ) {
            openDialog(
                R.string.use_gboard,
                R.string.module,
                R.string.use_module,
                R.string.gboard,
                false,
                {
                    it.dismiss()
                    preferenceManager.edit {
                        putBoolean("useMagisk", false)
                        putBoolean("usageSet", true)
                    }
                    Config.useMagisk = false
                    ThemeUtils::loadThemes asyncInto mainViewModel::setThemes
                }) {
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
            }
        } else if (intent.getBooleanExtra(
                "update",
                this@MainActivity.intent.getBooleanExtra("update", false)
            )
        ) {
            openDialog(R.string.update_ready, R.string.update) { update() }
        }
    }

    private fun update() {
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
            notify(notificationId, builder.build())
        }
        var finished = false
        UpdateHelper(updateUrl, this).apply {
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
                    manager.notify(notificationId, builder.build())
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
                manager.notify(notificationId, builder.build())
                it?.connectionException?.printStackTrace()
                Log.d("ERROR", it?.serverErrorMessage ?: "NOO")
            }
        }.start()
    }
}
