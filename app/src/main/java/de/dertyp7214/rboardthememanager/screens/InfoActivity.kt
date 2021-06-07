package de.dertyp7214.rboardthememanager.screens

import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import de.Maxr1998.modernpreferences.PreferencesAdapter
import de.Maxr1998.modernpreferences.helpers.categoryHeader
import de.Maxr1998.modernpreferences.helpers.pref
import de.Maxr1998.modernpreferences.helpers.screen
import de.dertyp7214.rboardthememanager.BuildConfig
import de.dertyp7214.rboardthememanager.Config.IS_MIUI
import de.dertyp7214.rboardthememanager.Config.MODULE_ID
import de.dertyp7214.rboardthememanager.Config.THEME_LOCATION
import de.dertyp7214.rboardthememanager.Config.themeCount
import de.dertyp7214.rboardthememanager.R
import de.dertyp7214.rboardthememanager.utils.GboardUtils.getGboardVersion
import de.dertyp7214.rboardthememanager.utils.MagiskUtils
import dev.chrisbanes.insetter.applyInsetter
import kotlinx.android.synthetic.main.activity_info.*

class InfoActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setDecorFitsSystemWindows(false)
        setContentView(R.layout.activity_info)

        val usingModule = MagiskUtils.getModules().any { it.id == MODULE_ID }

        setSupportActionBar(info_toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        setTitle(R.string.info)

        info_toolbar.applyInsetter {
            type(statusBars = true) {
                margin()
            }
        }

        val screen = screen(this) {
            pref("theme_count") {
                titleRes = R.string.theme_count
                summary = themeCount?.toString() ?: "0"
                iconRes = R.drawable.ic_themes
            }
            pref("theme_path") {
                titleRes = R.string.theme_path
                summary = THEME_LOCATION
                iconRes = R.drawable.ic_folder_open
            }
            pref("installation_method") {
                titleRes = R.string.installation_method
                summaryRes = if (usingModule) R.string.magisk else R.string.other
                iconRes = R.drawable.ic_download
            }
            pref("rboard_app_version") {
                titleRes = R.string.rboard_app_version
                summary = "${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE})"
                iconRes = R.drawable.ic_rboard
            }
            categoryHeader("device") {
                titleRes = R.string.device
            }
            pref("android_version") {
                titleRes = R.string.android_version
                summary = Build.VERSION.RELEASE_OR_CODENAME
                iconRes = R.drawable.ic_android
            }
            if (MagiskUtils.isMagiskInstalled())
                pref("root_version") {
                    titleRes = R.string.root_version
                    summary =
                        "Magisk: ${MagiskUtils.getMagiskVersionString().removeSuffix(":MAGISK")}"
                    iconRes = R.drawable.ic_root
                }
            pref("gboard_version") {
                titleRes = R.string.gboard_version
                summary = getGboardVersion(this@InfoActivity).split("-").first()
                iconRes = R.drawable.ic_gboard
            }
            pref("unsupported_oem") {
                titleRes = R.string.unsupported_oem
                summaryRes = if (IS_MIUI) R.string.yes else R.string.no
                iconRes = R.drawable.ic_trash
            }
        }

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = PreferencesAdapter(screen)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}