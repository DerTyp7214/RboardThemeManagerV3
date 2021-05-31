package de.dertyp7214.rboardthememanager.screens

import android.os.Bundle
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.FragmentContainerView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.topjohnwu.superuser.BusyBoxInstaller
import com.topjohnwu.superuser.Shell
import com.topjohnwu.superuser.io.SuFile
import de.dertyp7214.rboardthememanager.BuildConfig
import de.dertyp7214.rboardthememanager.Config
import de.dertyp7214.rboardthememanager.R
import de.dertyp7214.rboardthememanager.adapter.MenuAdapter
import de.dertyp7214.rboardthememanager.components.SearchBar
import de.dertyp7214.rboardthememanager.core.dpToPx
import de.dertyp7214.rboardthememanager.core.runAsCommand
import de.dertyp7214.rboardthememanager.core.setMargin
import de.dertyp7214.rboardthememanager.data.MenuItem
import de.dertyp7214.rboardthememanager.utils.FileUtils
import de.dertyp7214.rboardthememanager.utils.ThemeUtils
import de.dertyp7214.rboardthememanager.utils.asyncInto
import de.dertyp7214.rboardthememanager.viewmodels.ThemesViewModel
import java.io.File

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setUp()

        val themesViewModel = ViewModelProvider(this)[ThemesViewModel::class.java]

        val searchBar = findViewById<SearchBar>(R.id.searchBar)
        val bottomSheet = findViewById<NestedScrollView>(R.id.bottom_bar)
        val navigationHolder = findViewById<FragmentContainerView>(R.id.fragmentContainerView)

        val navigation = findViewById<BottomNavigationView>(R.id.navigation)
        val menuRecyclerView = findViewById<RecyclerView>(R.id.recyclerView)

        val secondaryContent = findViewById<LinearLayout>(R.id.secondaryContent)

        val bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)

        navigationHolder.setMargin(
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

        ThemeUtils::loadThemes asyncInto themesViewModel::setThemes
    }

    private fun setUp() {
        Shell.enableVerboseLogging = BuildConfig.DEBUG
        Shell.setDefaultBuilder(Shell.Builder.create().apply {
            setFlags(Shell.FLAG_MOUNT_MASTER)
            setInitializers(BusyBoxInstaller::class.java)
        })

        "rm -rf ${cacheDir.absolutePath}/*".runAsCommand()
        val files = ArrayList<File>()
        files += FileUtils.getThemePacksPath(this).listFiles()!!
        files += FileUtils.getSoundPacksPath(this).listFiles()!!
        files.forEach {
            SuFile(it.absolutePath).deleteRecursive()
        }

        if (ThemeUtils.checkForExistingThemes()) ThemeUtils.getThemesPathFromProps()
            ?.apply { Config.THEME_LOCATION = this }
    }
}