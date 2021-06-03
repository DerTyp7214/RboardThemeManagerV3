package de.dertyp7214.rboardthememanager.screens

import android.content.Intent
import android.graphics.RenderEffect
import android.graphics.Shader
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
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.FragmentContainerView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import de.dertyp7214.rboardthememanager.BuildConfig
import de.dertyp7214.rboardthememanager.Config
import de.dertyp7214.rboardthememanager.R
import de.dertyp7214.rboardthememanager.adapter.MenuAdapter
import de.dertyp7214.rboardthememanager.components.SearchBar
import de.dertyp7214.rboardthememanager.core.*
import de.dertyp7214.rboardthememanager.data.MenuItem
import de.dertyp7214.rboardthememanager.data.ModuleMeta
import de.dertyp7214.rboardthememanager.utils.*
import de.dertyp7214.rboardthememanager.viewmodels.ThemesViewModel
import java.io.File

class MainActivity : AppCompatActivity() {

    private val updateUrl =
        "https://raw.githubusercontent.com/DerTyp7214/RboardThemeManagerV3/master/app/${BuildConfig.BUILD_TYPE}/app-${BuildConfig.BUILD_TYPE}.apk"

    private lateinit var downloadResultLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setUp()

        val themesViewModel = ViewModelProvider(this)[ThemesViewModel::class.java]

        val searchBar = findViewById<SearchBar>(R.id.searchBar)
        val bottomSheet = findViewById<NestedScrollView>(R.id.bottom_bar)
        val refreshLayout = findViewById<SwipeRefreshLayout>(R.id.refreshLayout)
        val navigationHolder = findViewById<FragmentContainerView>(R.id.fragmentContainerView)

        val navigation = findViewById<BottomNavigationView>(R.id.navigation)
        val menuRecyclerView = findViewById<RecyclerView>(R.id.recyclerView)

        val secondaryContent = findViewById<LinearLayout>(R.id.secondaryContent)

        val bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)

        refreshLayout.isRefreshing = true
        refreshLayout.setProgressViewOffset(
            true,
            0,
            5.dpToPx(this).toInt()
        )
        refreshLayout.setProgressBackgroundColorSchemeColor(getAttrColor(R.attr.colorBackgroundFloating))
        refreshLayout.setColorSchemeColors(getAttrColor(R.attr.colorOnPrimary))

        refreshLayout.setMargin(
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
            override fun onStateChanged(bottomSheet: View, newState: Int) {}

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                navigation.alpha = 1 - slideOffset
                secondaryContent.alpha = slideOffset

                if (navigation.alpha == 0F) navigation.visibility = GONE
                else if (navigation.visibility == GONE) navigation.visibility = VISIBLE
            }
        })

        val menuAdapter = MenuAdapter(listOf(
            MenuItem(
                R.drawable.ic_download,
                R.string.app_name
            ) {
                Toast.makeText(this, "Hi 1", Toast.LENGTH_LONG).show()
            },
            MenuItem(
                R.drawable.ic_download,
                R.string.app_name
            ) {
                Toast.makeText(this, "Hi 2", Toast.LENGTH_LONG).show()
            },
            MenuItem(
                R.drawable.ic_download,
                R.string.app_name
            ) {
                Toast.makeText(this, "Hi 3", Toast.LENGTH_LONG).show()
            }
        ), this)

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

        themesViewModel.themesObserve(this) {
            refreshLayout.isRefreshing = false
        }

        refreshLayout.setOnRefreshListener {
            refreshLayout.isRefreshing = true
            searchBar.setText()
            themesViewModel.setFilter()
            ThemeUtils::loadThemes asyncInto themesViewModel::setThemes
        }

        ThemeUtils::loadThemes asyncInto themesViewModel::setThemes
    }

    private fun setUp() {
        downloadResultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                findViewById<View>(android.R.id.content).setRenderEffect(null)
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
                    "# Default Theme and Theme-location\nro.com.google.ime.theme_file=veu.zip\nro.com.google.ime.themes_dir=${Config.THEME_LOCATION}"
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
                content.setRenderEffect(
                    RenderEffect.createBlurEffect(
                        10F,
                        10F,
                        Shader.TileMode.REPEAT
                    )
                )
                PackageUtils.install(this@MainActivity, File(path), downloadResultLauncher) {
                    content.setRenderEffect(null)
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