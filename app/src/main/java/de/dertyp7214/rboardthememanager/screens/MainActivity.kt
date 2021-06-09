@file:Suppress("DEPRECATION")

package de.dertyp7214.rboardthememanager.screens

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Intent
import android.content.Intent.*
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.app.ShareCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.view.marginBottom
import androidx.core.widget.NestedScrollView
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dertyp7214.preferencesplus.core.dp
import com.dertyp7214.preferencesplus.core.setMargins
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.topjohnwu.superuser.io.SuFile
import de.dertyp7214.rboardthememanager.BuildConfig
import de.dertyp7214.rboardthememanager.Config
import de.dertyp7214.rboardthememanager.R
import de.dertyp7214.rboardthememanager.adapter.MenuAdapter
import de.dertyp7214.rboardthememanager.components.SearchBar
import de.dertyp7214.rboardthememanager.core.*
import de.dertyp7214.rboardthememanager.data.MenuItem
import de.dertyp7214.rboardthememanager.data.ModuleMeta
import de.dertyp7214.rboardthememanager.utils.*
import de.dertyp7214.rboardthememanager.utils.PackageUtils.isPackageInstalled
import de.dertyp7214.rboardthememanager.viewmodels.ThemesViewModel
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File

class MainActivity : AppCompatActivity() {

    private val updateUrl =
        "https://github.com/DerTyp7214/RboardThemeManagerV3/releases/download/latest-${BuildConfig.BUILD_TYPE}/app-${BuildConfig.BUILD_TYPE}.apk"

    private lateinit var downloadResultLauncher: ActivityResultLauncher<Intent>
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<NestedScrollView>
    private lateinit var themesViewModel: ThemesViewModel

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setUp()

        themesViewModel = ViewModelProvider(this)[ThemesViewModel::class.java]

        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        val searchBar = findViewById<SearchBar>(R.id.searchBar)
        val bottomSheet = findViewById<NestedScrollView>(R.id.bottom_bar)
        val navigationHolder =
            supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
        val controller = navigationHolder.navController

        val navigation = findViewById<BottomNavigationView>(R.id.navigation)
        val menuRecyclerView = findViewById<RecyclerView>(R.id.recyclerView)

        val secondaryContent = findViewById<LinearLayout>(R.id.secondaryContent)

        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)

        val reloadThemesLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                themesViewModel.setThemes()
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            }

        val closeBottomSheetBehaviorLaucher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            }

        val mainMenuItems = arrayListOf(
            MenuItem(
                R.drawable.ic_info,
                R.string.info
            ) {
                PreferencesActivity::class.java.start(this, closeBottomSheetBehaviorLaucher) {
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
                R.string.flags
            ) {
                PreferencesActivity::class.java.start(this, closeBottomSheetBehaviorLaucher) {
                    putExtra("type", "flags")
                }
            },
            MenuItem(
                R.drawable.ic_creator,
                R.string.rboard_theme_creator
            ) {
                if (isPackageInstalled(Config.RBOARD_THEME_CREATOR_PACKAGE_NAME, packageManager))
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

        findViewById<View>(R.id.fragmentContainerView).setMargin(
            bottomMargin = resources.getDimension(R.dimen.bottomBarHeight).toInt() + 18.dpToPx(this)
                .toInt()
        )

        bottomSheetBehavior.isFitToContents = true
        bottomSheetBehavior.skipCollapsed = true
        bottomSheetBehavior.peekHeight =
            resources.getDimension(R.dimen.bottomBarHeight).toInt() + 8.dpToPx(this).toInt()
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED

        bottomSheetBehavior.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
                    if (themesViewModel.getSelectedTheme() != null)
                        themesViewModel.setSelectedTheme()
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                navigation.alpha = 1 - slideOffset
                secondaryContent.alpha = slideOffset

                if (navigation.alpha == 0F) navigation.visibility = GONE
                else if (navigation.visibility == GONE) navigation.visibility = VISIBLE
            }
        })

        toolbar.navigationIcon =
            ContextCompat.getDrawable(this, R.drawable.ic_baseline_arrow_back_24)
        toolbar.setNavigationOnClickListener { themesViewModel.getSelections().second?.clearSelection() }

        toolbar.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.share -> {
                    val adapter = themesViewModel.getSelections().second
                    val themes = adapter?.getSelected()?.filter {
                        it.path.isNotEmpty() && !it.path.startsWith("assets:")
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
                            val uri = FileProvider.getUriForFile(this, packageName, zip)
                            ShareCompat.IntentBuilder(this)
                                .setStream(uri)
                                .setType("application/pack")
                                .intent.setAction(ACTION_SEND)
                                .setDataAndType(uri, "application/pack")
                                .addFlags(FLAG_GRANT_READ_URI_PERMISSION).apply {
                                    startActivity(
                                        createChooser(
                                            this,
                                            getString(R.string.share_themes)
                                        )
                                    )
                                }
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
                        val adapter = themesViewModel.getSelections().second
                        if (adapter != null) {
                            val themes = adapter.getSelected().filter {
                                it.path.isNotEmpty() && !it.path.startsWith("assets:")
                            }
                            if (themes.isNotEmpty()) {
                                themes.forEach { theme ->
                                    theme.delete()
                                }
                                themesViewModel.setThemes()
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
            }
            true
        }

        themesViewModel.observeSelections(this) { selections ->
            val originHeight = toolbar.marginBottom
            val destinationHeight = if (selections.first) 8.dp(this) else 62.dp(this)
            ValueAnimator.ofInt(originHeight, destinationHeight).apply {
                addUpdateListener {
                    toolbar.setMargins(0, 0, 0, it.animatedValue as Int)
                }
                duration = 150
                start()
            }
        }

        themesViewModel.observerSelectedTheme(this) { theme ->
            secondaryContent.removeViewAt(0)
            if (theme != null) {
                secondaryContent.addView(ThemeUtils.getThemeView(theme, this), 0)
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
                searchBar.clearFocus()
                menuItems.clear()
                menuItems.add(MenuItem(R.drawable.ic_apply_theme, R.string.apply_theme) {
                    bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
                    if (applyTheme(theme, true))
                        Toast.makeText(this, R.string.applied, Toast.LENGTH_SHORT).show()
                    else Toast.makeText(this, R.string.error, Toast.LENGTH_SHORT).show()
                    delayed(150) {
                        themesViewModel.setSelectedTheme()
                        themesViewModel.refreshThemes()
                    }
                })
                if (theme.path.isNotEmpty() && !theme.path.startsWith("assets:"))
                    menuItems.add(MenuItem(R.drawable.ic_delete_theme, R.string.delete_theme) {
                        openDialog(R.string.q_delete_theme, R.string.delete_theme) {
                            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
                            if (SuFile(theme.path).delete())
                                Toast.makeText(this, R.string.theme_deleted, Toast.LENGTH_SHORT)
                                    .show()
                            else Toast.makeText(this, R.string.error, Toast.LENGTH_SHORT).show()
                            delayed(150) {
                                themesViewModel.setSelectedTheme()
                                themesViewModel.setThemes(listOf())
                            }
                        }
                    })
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

        secondaryContent.addView(ThemeUtils.getThemeView(ThemeUtils.getActiveThemeData(), this), 0)

        menuRecyclerView.layoutManager = LinearLayoutManager(this)
        menuRecyclerView.setHasFixedSize(true)
        menuRecyclerView.adapter = menuAdapter

        searchBar.setOnSearchListener { text ->
            themesViewModel.setFilter(text)
        }

        searchBar.setOnCloseListener {
            themesViewModel.setFilter()
        }

        themesViewModel.onClearSearch(this) {
            searchBar.setText()
        }

        controller.addOnDestinationChangedListener { _, destination, _ ->
            themesViewModel.setFilter()
            themesViewModel.clearSearch()

            when (destination.id) {
                R.id.action_themeListFragment_to_downloadListFragment -> {
                }
                R.id.action_downloadListFragment_to_themeListFragment -> {
                }
            }
        }

        navigation.setOnNavigationItemSelectedListener {
            val currentDestination = controller.currentDestination?.id ?: -1
            when (it.itemId) {
                R.id.navigation_themes -> {
                    if (currentDestination == R.id.downloadListFragment) {
                        controller.navigate(R.id.action_downloadListFragment_to_themeListFragment)
                    }
                }
                R.id.navigation_downloads -> {
                    if (currentDestination == R.id.themeListFragment) {
                        controller.navigate(R.id.action_themeListFragment_to_downloadListFragment)
                    }
                }
                R.id.navigation_sounds -> {
                }
            }
            true
        }

        themesViewModel.onNavigate(this) { id ->
            navigation.selectedItemId = id
        }
    }

    override fun onBackPressed() {
        when {
            bottomSheetBehavior.state == BottomSheetBehavior.STATE_EXPANDED ->
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            themesViewModel.getSelections().first -> themesViewModel.getSelections().second?.clearSelection()
            searchBar.focus -> searchBar.setText()
            else -> super.onBackPressed()
        }
    }

    private fun setUp() {
        downloadResultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                findViewById<View>(android.R.id.content)
            }

        if (ThemeUtils.checkForExistingThemes()) ThemeUtils.getThemesPathFromProps()
            ?.apply { Config.THEME_LOCATION = this }

        if (!MagiskUtils.getModules().any { it.id == Config.MODULE_ID }) {
            val meta = ModuleMeta(
                Config.MODULE_ID,
                "Rboard Themes",
                "v30",
                "300",
                "RKBDI & DerTyp7214",
                "Module for Rboard Themes app"
            )
            val file = mapOf(
                Pair(
                    "system.prop",
                    "# Default Theme and Theme-location\n" +
                            "ro.com.google.ime.theme_file=veu.zip\n" +
                            "ro.com.google.ime.themes_dir=${Config.THEME_LOCATION}"
                ),
                Pair(Config.THEME_LOCATION, null)
            )
            MagiskUtils.installModule(meta, file)
            openDialog(R.string.reboot_to_continue, R.string.reboot, {
                finishAndRemoveTask()
            }) {
                "reboot".runAsCommand()
            }
        } else if (intent.extras?.getBoolean("update") == true)
            openDialog(R.string.update_ready, R.string.update) { update() }
    }

    private fun update() {
        val content = findViewById<View>(android.R.id.content)

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
                builder.setContentText(getString(R.string.download_complete))
                    .setProgress(0, 0, false)
                manager.notify(notificationId, builder.build())
                content
                PackageUtils.install(this@MainActivity, File(path), downloadResultLauncher) {
                    content
                    Toast.makeText(this@MainActivity, R.string.error, Toast.LENGTH_LONG).show()
                }
            }
            setErrorListener {
                builder.setContentText(getString(R.string.download_error))
                    .setProgress(0, 0, false)
                manager.notify(notificationId, builder.build())
                it?.connectionException?.printStackTrace()
                Log.d("ERROR", it?.serverErrorMessage ?: "NOO")
            }
        }.start()
    }
}
