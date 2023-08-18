package de.dertyp7214.rboardthememanager

import android.os.Build
import de.dertyp7214.rboardthememanager.core.getSystemProperty
import de.dertyp7214.rboardthememanager.data.ModuleMeta
import de.dertyp7214.rboardthememanager.utils.GboardUtils

@Suppress("MemberVisibilityCanBePrivate", "unused", "SdCardPath")
object Config {
    var useMagisk = false
    var newGboard = true
    val PLAY_URL = { packageName: String ->
        "https://play.google.com/store/apps/details?id=$packageName"
    }
    var THEME_LOCATION = "/system/etc/gboard_theme"

    var GITLAB_REPO_PREFIX = "https://gitlab.com/dertyp7214/RboardMirror/-/raw/main/PackRepoBeta"
    var GITHUB_REPO_PREFIX = "https://raw.githubusercontent.com/GboardThemes/PackRepoBeta/main"

    var GITLAB_RAW_PREFIX = "https://gitlab.com/dertyp7214/RboardMirror/-/raw/main/PackRepoBeta"
    var GITHUB_RAW_PREFIX = "https://github.com/GboardThemes/PackRepoBeta/raw/main"

    var REPO_PREFIX = GITHUB_REPO_PREFIX
    var RAW_PREFIX = GITHUB_RAW_PREFIX

    const val MAGISK_PACKAGE_NAME = "com.topjohnwu.magisk"
    const val PATCHER_PACKAGE = "de.dertyp7214.rboardpatcher"
    const val MODULES_PATH = "/data/adb/modules"
    const val MODULE_ID = "rboard-themes"
    const val MODULE_PATH = "$MODULES_PATH/$MODULE_ID"
    const val GBOARD_PACKAGE_NAME = "com.google.android.inputmethod.latin"
    const val RBOARD_THEME_CREATOR_PACKAGE_NAME = "de.dertyp7214.rboardthemecreator"

    const val FLAG_PATH = "/data/data/$GBOARD_PACKAGE_NAME/shared_prefs/flag_value.xml"

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
                if (it) "/data/user${if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) "_de" else ""}/0/$GBOARD_PACKAGE_NAME/shared_prefs/${GBOARD_PACKAGE_NAME}_preferences.xml"
                else "/data/data/$GBOARD_PACKAGE_NAME/shared_prefs/${GBOARD_PACKAGE_NAME}_preferences.xml"
            }
        }

    val MAGISK_THEME_LOC: String
        get() {
            return if (!useMagisk) "/data/data/$GBOARD_PACKAGE_NAME/files/themes"
            else if (!THEME_LOCATION.startsWith("/system")) THEME_LOCATION else "$MODULE_PATH$THEME_LOCATION"
        }

    val PACKS_URLS
        get() = listOf(
            "true:${REPO_PREFIX}/list.json",
            "true:https://raw.githubusercontent.com/AkosPaha01/PackRepoBeta/main/list.json",
            "true:https://raw.githubusercontent.com/GboardThemes/RboardCommunityThemes/main/list.json"
        )
    val REPOS
        get() = ArrayList(PACKS_URLS)

    // TODO: add repo button should load list of verified repos
    const val REPOS_LIST_URL =
        "https://raw.githubusercontent.com/GboardThemes/Repositories/main/repos.json"
    const val SOUNDS_PACKS_URL =
        "https://raw.githubusercontent.com/GboardThemes/Soundpack/master/download_sounds.json"

    var themeCount: Int? = null

    var darkTheme: String? = null
    var lightTheme: String? = null
}
