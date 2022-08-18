package de.dertyp7214.rboardthememanager.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.graphics.*
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.app.ShareCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.graphics.drawable.toBitmap
import androidx.core.graphics.get
import com.dertyp7214.logs.helpers.Logger
import com.google.android.material.card.MaterialCardView
import com.google.gson.Gson
import com.topjohnwu.superuser.io.SuFile
import de.dertyp7214.colorutilsc.ColorUtilsC
import de.dertyp7214.rboardthememanager.Application
import de.dertyp7214.rboardthememanager.Config
import de.dertyp7214.rboardthememanager.Config.GBOARD_PACKAGE_NAME
import de.dertyp7214.rboardthememanager.Config.REPOS
import de.dertyp7214.rboardthememanager.R
import de.dertyp7214.rboardthememanager.core.*
import de.dertyp7214.rboardthememanager.data.ThemeDataClass
import de.dertyp7214.rboardthememanager.data.ThemePack
import de.dertyp7214.rboardthememanager.preferences.Flags
import de.dertyp7214.rboardthememanager.preferences.Settings
import de.dertyp7214.rboardthememanager.proto.ThemePackList
import java.io.BufferedInputStream
import java.io.File
import java.net.URL
import java.util.*

enum class InternalThemeNames(val path: String) {
    DOWNLOAD_THEMES("rboard:download_themes")
}

@SuppressLint("SdCardPath")
fun applyTheme(
    theme: ThemeDataClass,
    withBorders: Boolean = false
): Boolean {
    val name =
        if (theme.path.isEmpty() || theme.path.startsWith("assets:") || theme.path.startsWith("system_auto:")) theme.path
        else "${if (Config.useMagisk) "system:" else "files:themes/"}${theme.name}.zip"
    val inputPackageName = GBOARD_PACKAGE_NAME
    val fileName = Config.GBOARD_PREFS_PATH
    Logger.log(
        Logger.Companion.Type.INFO,
        "APPLY",
        "[ApplyTheme]: $name $inputPackageName $fileName"
    )

    val content = SuFile(fileName).openStream()?.use {
        it.bufferedReader().readText()
    }?.let {
        var changed = it

        changed = if ("<string name=\"additional_keyboard_theme\">" in changed)
            changed.replace(
                "<string name=\"additional_keyboard_theme\">.*</string>".toRegex(),
                "<string name=\"additional_keyboard_theme\">$name</string>"
            )
        else
            changed.replace(
                "<map>",
                "<map><string name=\"additional_keyboard_theme\">$name</string>"
            )

        // Change enable_key_border value
        changed = if ("<boolean name=\"enable_key_border\"" in changed) {
            changed.replace(
                "<boolean name=\"enable_key_border\" value=\".*\" />".toRegex(),
                "<boolean name=\"enable_key_border\" value=\"$withBorders\" />"
            )
        } else {
            changed.replace(
                "<map>",
                "<map><boolean name=\"enable_key_border\" value=\"$withBorders\" />"
            )
        }
        return@let changed
    }

    if (content != null)
        SuFile(fileName).writeFile(content.trim())

    return "am force-stop $inputPackageName".runAsCommand()
}

@SuppressLint("SdCardPath")
fun getActiveTheme(): String {
    val fileLol =
        SuFile(Config.GBOARD_PREFS_PATH)
    return try {
        fileLol.openStream()?.bufferedReader()?.readText()
            ?.split("<string name=\"additional_keyboard_theme\">")
            ?.let { if (it.size > 1) it[1].split("</string>")[0] else "" }
            ?.removePrefix("files:themes/")
            ?.removePrefix("system:")
            ?.removeSuffix(".zip") ?: ""
    } catch (error: Exception) {
        Logger.log(Logger.Companion.Type.ERROR, "ActiveTheme", error.message)
        ""
    }
}

object ThemeUtils {
    fun loadThemes(): List<ThemeDataClass> {
        val themePacks = loadThemePacks()
        val themeDir =
            SuFile(Config.MAGISK_THEME_LOC)
        return (themeDir.listFiles()?.filter {
            it.name.lowercase(Locale.ROOT).endsWith(".zip")
        }?.map {
            val imageFile = SuFile(Config.MAGISK_THEME_LOC, it.name.removeSuffix(".zip"))
            if (imageFile.exists()) ThemeDataClass(
                imageFile.decodeBitmap(),
                it.name.removeSuffix(".zip"),
                it.absolutePath
            )
            else ThemeDataClass(null, it.name.removeSuffix(".zip"), it.absolutePath)
        }.apply { if (this != null) Config.themeCount = size } ?: ArrayList()).let {
            it.forEach { theme ->
                themePacks[theme]?.let { pack ->
                    theme.packName = pack.name
                    theme.isInstalled = theme.isInstalled()
                    theme.updateAvailable = pack.date > theme.getLocalTime()
                }
            }
            val themes = arrayListOf<ThemeDataClass>()
            val context = Application.context
            if (context?.let { ctx ->
                    Settings.SETTINGS.SHOW_SYSTEM_THEME.getValue(
                        ctx,
                        true
                    )
                } == true) {
                if (Flags.values.monet) getDynamicColorsTheme()?.let { theme -> themes.add(theme) }
                themes.add(getSystemAutoTheme())
            }
            if (context?.let { ctx ->
                    Settings.SETTINGS.SHOW_PREINSTALLED_THEMES.getValue(
                        ctx,
                        true
                    )
                } == true) themes.addAll(buildPreinstalledThemesList())
            themes.addAll(it)
            if (themes.isEmpty()) context?.let { ctx ->
                themes.add(
                    ThemeDataClass(
                        BitmapFactory.decodeStream(
                            BufferedInputStream(
                                ctx.resources.openRawResource(
                                    R.raw.system_auto
                                )
                            )
                        ),
                        ctx.getString(R.string.download_themes),
                        InternalThemeNames.DOWNLOAD_THEMES.path
                    )
                )
            }
            themes
        }
    }

    fun loadThemePacks(): List<ThemePack> {
        return try {
            val packs = arrayListOf<ThemePack>()
            REPOS.filterActive().forEach { repo ->
                try {
                    val rboardRepo = repo.parseRepo()
                    val protoListPath = rboardRepo?.meta?.protoList
                    if (protoListPath != null) {
                        val protoListUrl = rboardRepo.url.replace("list.json", protoListPath)

                        val protoList = URL(protoListUrl).readBytes()

                        ThemePackList.ObjectList.parseFrom(protoList).objectsList?.toPackList()?.map {
                            it.repoUrl = repo
                            it
                        }?.let(packs::addAll)
                    } else packs.addAll(
                        Gson().fromJson<Collection<ThemePack>?>(
                            URL(repo).readText(),
                            TypeTokens<List<ThemePack>>()
                        ).map {
                            it.repoUrl = repo
                            it
                        }
                    )
                } catch (_: Exception) {
                }
            }
            ArrayList(packs.sortedBy { it.name.lowercase() })
        } catch (_: Exception) {
            listOf()
        }
    }

    fun getThemesPathFromProps(): String? {
        var path: String? = null
        "getprop ro.com.google.ime.themes_dir".runAsCommand {
            if (it.first().isNotEmpty()) path = it.first()
            Logger.log(Logger.Companion.Type.DEBUG, "Gboard-Themes-Path", path)
        }
        return path
    }

    fun checkForExistingThemes(): Boolean {
        return getThemesPathFromProps() != null
    }

    private fun buildPreinstalledThemesList(): List<ThemeDataClass> {
        val themes = arrayListOf<ThemeDataClass>()
        val themeNames = listOf(
            "color_black", "color_blue", "color_blue_grey", "color_brown", "color_cyan",
            "color_deep_purple", "color_green", "color_light_pink", "color_pink", "color_red",
            "color_sand", "color_teal", "google_blue_dark", "google_blue_light", "holo_blue",
            "holo_white", "material_dark", "material_light"
        )

        themeNames.forEach { name ->
            val path = "assets:theme_package_metadata_$name.binarypb"
            val image = Application.context?.let {
                try {
                    val inputStream = it.resources.openRawResource(
                        FileUtils.getResourceId(it, name, "raw", it.packageName)
                    )
                    BitmapFactory.decodeStream(BufferedInputStream(inputStream))
                } catch (e: Exception) {
                    null
                }
            }
            themes.add(ThemeDataClass(image, name, path))
        }

        return themes
    }

    fun getActiveThemeData(): ThemeDataClass = getThemeData(getActiveTheme())

    fun getThemeData(themeName: String): ThemeDataClass {
        return if ((themeName.startsWith("assets:") || themeName.startsWith("system_auto:")) && Application.context != null) {
            val imgName =
                themeName
                    .removePrefix("assets:theme_package_metadata_")
                    .removeSuffix(".binarypb")
                    .removeSuffix(":")
            val image = if (themeName.startsWith("system_auto:")) getSystemAutoImage() else {
                Application.context?.let {
                    try {
                        val inputStream = it.resources.openRawResource(
                            FileUtils.getResourceId(
                                it,
                                imgName,
                                "raw",
                                it.packageName
                            )
                        )
                        BitmapFactory.decodeStream(BufferedInputStream(inputStream))
                    } catch (e: Exception) {
                        null
                    }
                }
            }
            ThemeDataClass(
                image,
                imgName.split("_").joinToString(" ") {
                    it.replaceFirstChar { char ->
                        if (char.isLowerCase()) char.titlecase(
                            Locale.getDefault()
                        ) else char.toString()
                    }
                }, themeName
            )
        } else if (themeName.isNotEmpty()) {
            val name = themeName.split("/").last()
            val image = SuFile(Config.MAGISK_THEME_LOC, name.removeSuffix(".zip"))
            ThemeDataClass(
                image.decodeBitmap(),
                image.name,
                SuFile(Config.MAGISK_THEME_LOC, name).absolutePath
            )
        } else {
            getDynamicColorsTheme() ?: ThemeDataClass(null, "", "")
        }
    }

    private fun getDynamicColorsTheme(): ThemeDataClass? {
        return if (Application.context != null) {
            val image = Application.context?.let { context ->
                val inputStream = context.resources.openRawResource(
                    FileUtils.getResourceId(
                        context,
                        "system_auto",
                        "raw",
                        context.packageName
                    )
                )
                BitmapFactory.decodeStream(BufferedInputStream(inputStream))
            }
            ThemeDataClass(
                image,
                "dynamic_color",
                "",
                colorFilter = PorterDuffColorFilter(
                    Application.context!!.getAttr(android.R.attr.colorAccent),
                    PorterDuff.Mode.OVERLAY
                )
            )
        } else null
    }

    fun getSystemAutoTheme(): ThemeDataClass {
        return ThemeDataClass(
            getSystemAutoImage(),
            "system_auto:",
            "system_auto:"
        )
    }

    private fun getSystemAutoImage(): Bitmap? {
        return Application.context?.let { context ->
            (try {
                SuFile(
                    Config.MAGISK_THEME_LOC,
                    ((context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES)
                        .let { if (it) Config.darkTheme else Config.lightTheme }
                        ?.removeSuffix(".zip")
                ).decodeBitmap()
            } catch (e: Exception) {
                null
            }) ?: BitmapFactory.decodeStream(
                BufferedInputStream(
                    context.resources.openRawResource(
                        R.raw.system_auto
                    )
                )
            )
        }
    }

    @SuppressLint("InflateParams")
    fun getThemeView(theme: ThemeDataClass, context: Context): View {
        return LinearLayout(context).apply {
            id = R.id.current_theme_view
            orientation = LinearLayout.VERTICAL
            setPadding(8.dp(context), 8.dp(context), 8.dp(context), 0)
            addView(LayoutInflater.from(context).inflate(R.layout.single_theme_item, null).apply {
                val themeName = findViewById<TextView>(R.id.theme_name)
                val themeIcon = findViewById<ImageView>(R.id.theme_image)
                val card: MaterialCardView = findViewById(R.id.card)
                val gradient: View? = try {
                    findViewById(R.id.gradient)
                } catch (e: Exception) {
                    null
                }

                val defaultImage = ContextCompat.getDrawable(
                    context,
                    R.drawable.ic_keyboard
                )!!.toBitmap()

                themeIcon.setImageBitmap(theme.image ?: defaultImage)
                themeIcon.colorFilter = theme.colorFilter

                val color = themeIcon.drawable.toBitmap().let {
                    it[5, it.height / 2]
                }
                val isDark = ColorUtilsC.calculateLuminance(color) > .4

                if (gradient != null) {
                    val g = GradientDrawable(
                        GradientDrawable.Orientation.LEFT_RIGHT,
                        intArrayOf(color, Color.TRANSPARENT)
                    )
                    gradient.background = g
                }

                card.setCardBackgroundColor(color)

                themeName.text = theme.readableName
                themeName.setTextColor(if (!isDark) Color.WHITE else Color.BLACK)
            })
            addView(View(context).apply {
                setHeight(8.dp(context))
            })
            addView(View(context).apply {
                setHeight(1.dp(context) / 2)
                setWidth(LinearLayout.LayoutParams.MATCH_PARENT)
                setBackgroundColor(context.getAttr(R.attr.colorOnSurface))
                alpha = .6F
            })
        }
    }

    fun shareTheme(
        activity: Activity,
        themePack: File,
        install: Boolean = true,
        packageName: String? = null
    ) {
        val uri = FileProvider.getUriForFile(
            activity,
            activity.packageName,
            themePack
        )
        ShareCompat.IntentBuilder(activity)
            .setStream(uri)
            .setType("application/pack")
            .intent
            .setAction(if (install) Intent.ACTION_VIEW else Intent.ACTION_SEND)
            .setDataAndType(uri, "application/pack")
            .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION).apply {
                packageName?.let { pkg -> setPackage(pkg) }
                activity.startActivity(
                    Intent.createChooser(
                        this,
                        activity.getString(R.string.share_theme)
                    )
                )
            }
    }
}