package de.dertyp7214.rboardthememanager

import de.dertyp7214.rboardthememanager.core.getSystemProperty
import de.dertyp7214.rboardthememanager.data.ModuleMeta

@Suppress("MemberVisibilityCanBePrivate", "unused", "SdCardPath")
object Config {
    var useMagisk = false
    var newGboard = true

    var THEME_LOCATION = "/system/etc/gboard_theme"

    const val MODULES_PATH = "/data/adb/modules"
    const val MODULE_ID = "rboard-themes"
    const val MODULE_PATH = "$MODULES_PATH/$MODULE_ID"
    const val GBOARD_PACKAGE_NAME = "com.google.android.inputmethod.latin"
    const val RBOARD_THEME_CREATOR_PACKAGE_NAME = "de.dertyp7214.rboardthemecreator"
    
    val MODULE_META = ModuleMeta(
        MODULE_ID,
        "Rboard Themes",
        "v30",
        "300",
        "RKBDI & DerTyp7214",
        "Module for Rboard Themes app"
    )
    
    val IS_MIUI = "ro.miui.ui.version.name".getSystemProperty().isNotEmpty()
    
    val GBOARD_PREFS_PATH: String
        get() {
            return newGboard.let {
                if (it) "/data/user_de/0/$GBOARD_PACKAGE_NAME/shared_prefs/${GBOARD_PACKAGE_NAME}_preferences.xml"
                else "/data/data/$GBOARD_PACKAGE_NAME/shared_prefs/${GBOARD_PACKAGE_NAME}_preferences.xml"
            }
        }

    val MAGISK_THEME_LOC: String
        get() {
            return if (!useMagisk) "/data/data/$GBOARD_PACKAGE_NAME/files/themes"
            else if (!THEME_LOCATION.startsWith("/system")) THEME_LOCATION else "$MODULE_PATH$THEME_LOCATION"
        }

    const val PACKS_URL =
        "https://raw.githubusercontent.com/GboardThemes/Packs/master/download_list.json"
    val REPOS = arrayListOf(PACKS_URL)

    const val SOUNDS_PACKS_URL =
        "https://raw.githubusercontent.com/GboardThemes/Soundpack/master/download_sounds.json"

    var themeCount: Int? = null
}