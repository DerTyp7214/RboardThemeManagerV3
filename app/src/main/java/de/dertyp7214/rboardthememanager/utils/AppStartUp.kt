package de.dertyp7214.rboardthememanager.utils

import android.animation.ObjectAnimator
import android.app.NotificationChannel
import android.app.NotificationManager
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.view.View
import android.view.ViewTreeObserver
import android.view.animation.AnticipateInterpolator
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.animation.doOnEnd
import androidx.core.content.FileProvider
import androidx.core.content.edit
import androidx.preference.PreferenceManager
import com.google.firebase.messaging.FirebaseMessaging
import com.google.gson.Gson
import com.topjohnwu.superuser.BusyBoxInstaller
import com.topjohnwu.superuser.Shell
import com.topjohnwu.superuser.io.SuFile
import de.dertyp7214.rboardthememanager.BuildConfig
import de.dertyp7214.rboardthememanager.Config
import de.dertyp7214.rboardthememanager.R
import de.dertyp7214.rboardthememanager.core.*
import de.dertyp7214.rboardthememanager.data.OutputMetadata
import de.dertyp7214.rboardthememanager.preferences.Flags
import de.dertyp7214.rboardthememanager.screens.InstallPackActivity
import de.dertyp7214.rboardthememanager.screens.ShareFlags
import de.dertyp7214.rboardthememanager.widgets.FlagsWidget
import de.dertyp7214.rboardthememanager.widgets.SwitchKeyboardWidget
import org.json.JSONObject
import java.io.File
import java.io.InputStream
import java.net.URL

class AppStartUp(private val activity: AppCompatActivity) {
    private val checkUpdateUrl by lazy {
        "https://github.com/DerTyp7214/RboardThemeManagerV3/releases/download/latest-${BuildConfig.BUILD_TYPE}/output-metadata.json"
    }
    private val gboardPlayStoreUrl by lazy {
        "https://play.google.com/store/apps/details?id=${Config.GBOARD_PACKAGE_NAME}"
    }
    private val flagsUrl by lazy {
        "https://raw.githubusercontent.com/GboardThemes/PackRepoBeta/main/flags.json"
    }

    private var checkedForUpdate = false
    private var rootAccess = false
    private var gboardInstalled = false
    private var isReady = false

    private val preferences by lazy { PreferenceManager.getDefaultSharedPreferences(activity) }

    private fun AppCompatActivity.openImportFlags(
        resultLauncher: ActivityResultLauncher<Intent>,
        block: () -> Map<String, Any>
    ) {
        isReady = true
        val dialog = openLoadingDialog(R.string.processing_flags)
        doAsync(block) {
            dialog.dismiss()
            ShareFlags::class.java.start(this, resultLauncher) {
                putExtra("import", true)
                putExtra("flags", it)
            }
        }
    }

    fun setUp() {
        activity.splashScreen.setOnExitAnimationListener { splashScreenView ->
            val slideUp = ObjectAnimator.ofFloat(
                splashScreenView,
                View.TRANSLATION_Y,
                0f,
                -splashScreenView.height.toFloat()
            )
            slideUp.interpolator = AnticipateInterpolator()
            slideUp.duration = 200L

            slideUp.doOnEnd { splashScreenView.remove() }

            slideUp.start()
        }
    }

    fun onCreate(onCreate: AppCompatActivity.(Intent) -> Unit) {
        val block: AppCompatActivity.(Intent) -> Unit = {
            isReady = true
            onCreate(this, it)
        }
        activity.apply {
            content.viewTreeObserver.addOnPreDrawListener(object :
                ViewTreeObserver.OnPreDrawListener {
                override fun onPreDraw(): Boolean {
                    return if (isReady) {
                        content.viewTreeObserver.removeOnPreDrawListener(this)
                        true
                    } else false
                }
            })

            Shell.enableVerboseLogging = BuildConfig.DEBUG
            Shell.setDefaultBuilder(Shell.Builder.create().apply {
                setFlags(Shell.FLAG_MOUNT_MASTER)
                setInitializers(BusyBoxInstaller::class.java)
            })

            rootAccess = hasRoot()

            Config.REPOS.apply {
                val tmp = this.toSet()
                clear()
                addAll(
                    preferences.getStringSet("repos", tmp) ?: tmp
                )
            }

            if (rootAccess) doInBackground {
                "rm -rf \"${cacheDir.absolutePath}/*\"".runAsCommand()
                val files = ArrayList<File>()
                files.forEach {
                    SuFile(it.absolutePath).deleteRecursive()
                }
            }

            doAsync({
                GboardUtils.getGboardVersionCode(this) >= 60780714
            }, Config::newGboard::set)

            doInBackground {
                AppWidgetManager.getInstance(this).let { appWidgetManager ->
                    appWidgetManager.getAppWidgetIds(
                        ComponentName(this, FlagsWidget::class.java)
                    ).forEach { id ->
                        FlagsWidget.updateAppWidget(this, appWidgetManager, id)
                    }
                    appWidgetManager.getAppWidgetIds(
                        ComponentName(this, SwitchKeyboardWidget::class.java)
                    ).forEach { id ->
                        SwitchKeyboardWidget.updateAppWidget(this, appWidgetManager, id)
                    }
                }
            }

            File(applicationInfo.dataDir, "flags.json").apply {
                val timeStamp = try {
                    let {
                        if (!it.exists()) -1
                        else JSONObject().safeParse(it.readText()).getLong("time", -1)
                    }
                } catch (e: Exception) {
                    delete()
                    -1
                }
                doAsync(URL(flagsUrl)::getTextFromUrl) {
                    val flagFiles = listOf(
                        JSONObject().safeParse(resources.openRawResource(
                            FileUtils.getResourceId(
                                activity,
                                "flags",
                                "raw",
                                packageName
                            )
                        ).bufferedReader().use { reader -> reader.readText() }),
                        JSONObject().safeParse(it)
                    )
                    val latestJson =
                        flagFiles.reduce { acc, safeJSON ->
                            if (acc.getLong("time") > safeJSON.getLong(
                                    "time"
                                )
                            ) acc else safeJSON
                        }
                    val time = latestJson.getLong("time")
                    if (!exists() || time > timeStamp)
                        writeText(latestJson.toString())
                }
            }

            val initialized = preferences.getBoolean("initialized", false)

            val scheme = intent.scheme
            val data = intent.data

            Config.useMagisk = preferences.getBoolean("useMagisk", false)

            if (rootAccess) {
                "getprop ro.com.google.ime.d_theme_file".runAsCommand {
                    if (it.first().isNotEmpty()) Config.darkTheme = it.first()
                }
                "getprop ro.com.google.ime.theme_file".runAsCommand {
                    if (it.first().isNotEmpty()) Config.lightTheme = it.first()
                }

                val importFlagsResultLauncher =
                    registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                        val resultData = result.data
                        if (result.resultCode == AppCompatActivity.RESULT_OK && resultData != null) {
                            val size = resultData.getIntExtra("size", 0)
                            Toast.makeText(
                                this,
                                getString(R.string.flags_loaded, size),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }

                GboardUtils.loadBackupFlags { flags ->
                    isReady = true
                    openDialog(R.string.load_flags_long, R.string.load_flags) {
                        val oldFlags = flags.readXML()
                        val newFlags = HashMap<String, Any>()
                        SuFile(Flags.FILES.FLAGS.filePath).readXML().forEach { (key, value) ->
                            if (!oldFlags.containsKey(key)) newFlags[key] = value
                        }
                        SuFile(Flags.FILES.FLAGS.filePath).writeFile(flags.trim())
                        GboardUtils.updateCurrentFlags(flags)
                        openImportFlags(importFlagsResultLauncher) {
                            newFlags
                        }
                        "am force-stop ${Config.GBOARD_PACKAGE_NAME}".runAsCommand()
                    }
                }
            }

            when {
                initialized && scheme != "content" && data != null -> {
                    if (data.scheme == "file") {
                        val file = SuFile(data.path).let {
                            File(filesDir, "theme.pack").apply {
                                ProcessBuilder().su(
                                    "rm \"$absolutePath\"",
                                    "cp \"${it.absolutePath}\" \"$absolutePath\"",
                                    "chmod 644 \"$absolutePath\""
                                ).logs("File Import", false)
                            }
                        }
                        val uri = FileProvider.getUriForFile(this, packageName, file)
                        Intent(Intent.ACTION_VIEW).apply {
                            setDataAndType(uri, "application/pack")
                            flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
                            startActivity(this)
                            finish()
                        }
                    } else when (data.host?.split(".")?.first()) {
                        "repos" -> {
                            data.queryParameterNames.forEach {
                                when (it) {
                                    "add" -> {
                                        preferences.apply {
                                            val repos = HashMap(
                                                ArrayList(
                                                    getStringSet("repos", setOf())?.toList()
                                                        ?: listOf()
                                                ).toMap()
                                            )
                                            repos[data.getQueryParameter("add")] = true
                                            edit { putStringSet("repos", repos.toSet()) }
                                        }
                                        Toast.makeText(
                                            this,
                                            R.string.repo_added,
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }
                            }
                        }
                    }
                    finishAndRemoveTask()
                }
                initialized && data?.toString()?.endsWith(".rboard") == true -> {
                    val resultLauncher =
                        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                            val resultData = result.data
                            if (result.resultCode == AppCompatActivity.RESULT_OK && resultData != null) {
                                val size = resultData.getIntExtra("size", 0)
                                Toast.makeText(
                                    this,
                                    getString(R.string.flags_loaded, size),
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                            finishAndRemoveTask()
                        }
                    openImportFlags(resultLauncher) {
                        File(cacheDir, "flags.rboard").apply {
                            delete()
                            data.writeToFile(activity, this)
                        }.readXML()
                    }
                }
                initialized && data != null -> {
                    val resultLauncher =
                        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                            finishAndRemoveTask()
                        }
                    isReady = true
                    openLoadingDialog(R.string.unpacking_themes)
                    doAsync({
                        val zip = File(cacheDir, "themes.pack").apply {
                            delete()
                            data.writeToFile(activity, this)
                        }
                        if (!zip.exists()) listOf()
                        else {
                            val destination = File(cacheDir, zip.nameWithoutExtension)
                            SuFile(destination.absolutePath).deleteRecursive()
                            if (ZipHelper().unpackZip(destination.absolutePath, zip.absolutePath)) {
                                destination.listFiles { file -> file.extension == "zip" }
                                    ?.map { it.absolutePath }
                                    ?: listOf()
                            } else listOf()
                        }
                    }) {
                        InstallPackActivity::class.java.start(this, resultLauncher) {
                            putStringArrayListExtra("themes", ArrayList(it))
                        }
                    }
                }
                else -> {
                    gboardInstalled =
                        PackageUtils.isPackageInstalled(Config.GBOARD_PACKAGE_NAME, packageManager)

                    createNotificationChannels(this)
                    FirebaseMessaging.getInstance()
                        .subscribeToTopic("update-v3-${BuildConfig.BUILD_TYPE.lowercase()}")

                    isReady = !gboardInstalled || !rootAccess
                    when {
                        !gboardInstalled -> openDialog(
                            R.string.install_gboard,
                            R.string.gboard_not_installed
                        ) {
                            openUrl(gboardPlayStoreUrl)
                        }
                        !rootAccess -> openDialog(
                            R.string.cant_use_app,
                            R.string.not_rooted,
                            false,
                            null
                        ) {
                            finishAndRemoveTask()
                        }
                        else -> checkForUpdate { update ->
                            checkedForUpdate = true
                            isReady = true
                            validApp(this) {
                                preferences.edit { putBoolean("initialized", true) }

                                if (it) block(this, Intent().putExtra("update", update))
                                else finish()
                            }
                        }
                    }
                }
            }
        }
    }

    private fun checkForUpdate(callback: (update: Boolean) -> Unit) {
        if (preferences.getLong(
                "lastCheck",
                0
            ) + 5 * 60 * 100 > System.currentTimeMillis()
        ) callback(false)
        else doAsync(URL(checkUpdateUrl)::getTextFromUrl) { text ->
            try {
                val outputMetadata = Gson().fromJson(text, OutputMetadata::class.java)
                val versionCode = outputMetadata.elements.first().versionCode
                preferences.edit { putLong("lastCheck", System.currentTimeMillis()) }
                callback(versionCode > BuildConfig.VERSION_CODE)
            } catch (e: Exception) {
                callback(false)
            }
        }
    }

    private fun validApp(activity: AppCompatActivity, callback: (valid: Boolean) -> Unit) {
        preferences.apply {
            var valid = getBoolean("verified", true)
            if (valid) callback(valid)
            else activity.openDialog(R.string.unreleased, R.string.notice, false, {
                it.dismiss()
                callback(valid)
            }) {
                it.dismiss()
                valid = true
                callback(valid)
                edit { putBoolean("verified", true) }
            }
        }
    }

    private fun createNotificationChannels(activity: AppCompatActivity) {
        activity.apply {
            val namePush = getString(R.string.channel_name)
            val channelIdPush = getString(R.string.default_notification_channel_id)
            val descriptionTextPush = getString(R.string.channel_description)
            val importancePush = NotificationManager.IMPORTANCE_DEFAULT
            val channelPush = NotificationChannel(channelIdPush, namePush, importancePush).apply {
                description = descriptionTextPush
            }

            val nameDownload = getString(R.string.channel_name_download)
            val channelIdDownload = getString(R.string.download_notification_channel_id)
            val descriptionTextDownload = getString(R.string.channel_description_download)
            val importanceDownload = NotificationManager.IMPORTANCE_LOW
            val channelDownload =
                NotificationChannel(channelIdDownload, nameDownload, importanceDownload).apply {
                    description = descriptionTextDownload
                }

            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channelPush)
            notificationManager.createNotificationChannel(channelDownload)
        }
    }
}