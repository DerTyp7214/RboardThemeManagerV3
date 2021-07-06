package de.dertyp7214.rboardthememanager.screens

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.ViewTreeObserver
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit
import androidx.preference.PreferenceManager
import com.google.gson.Gson
import com.topjohnwu.superuser.BusyBoxInstaller
import com.topjohnwu.superuser.Shell
import com.topjohnwu.superuser.io.SuFile
import de.dertyp7214.rboardthememanager.BuildConfig
import de.dertyp7214.rboardthememanager.Config
import de.dertyp7214.rboardthememanager.R
import de.dertyp7214.rboardthememanager.core.*
import de.dertyp7214.rboardthememanager.data.OutputMetadata
import de.dertyp7214.rboardthememanager.utils.*
import de.dertyp7214.rboardthememanager.utils.PackageUtils.isPackageInstalled
import org.json.JSONObject
import java.io.File
import java.net.URL

class SplashScreen : AppCompatActivity() {

    private val checkUpdateUrl by lazy {
        "https://github.com/DerTyp7214/RboardThemeManagerV3/releases/download/latest-${BuildConfig.BUILD_TYPE}/output-metadata.json"
    }
    private val gboardPlayStoreUrl by lazy {
        "https://play.google.com/store/apps/details?id=${Config.GBOARD_PACKAGE_NAME}"
    }

    private var checkedForUpdate = false
    private var rootAccess = false
    private var gboardInstalled = false

    private val isReady: Boolean
        get() {
            return checkedForUpdate || !rootAccess
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        Shell.enableVerboseLogging = BuildConfig.DEBUG
        Shell.setDefaultBuilder(Shell.Builder.create().apply {
            setFlags(Shell.FLAG_MOUNT_MASTER)
            setInitializers(BusyBoxInstaller::class.java)
        })

        "rm -rf ${cacheDir.absolutePath}/*".runAsCommand()
        val files = ArrayList<File>()
        files.forEach {
            SuFile(it.absolutePath).deleteRecursive()
        }

        File(applicationInfo.dataDir, "flags.json").apply {
            val timeStamp = try {
                let {
                    if (!it.exists()) -1
                    else SafeJSON(JSONObject(it.readText())).getLong("time", -1)
                }
            } catch (e: Exception) {
                delete()
                -1
            }
            doAsync(URL("https://raw.githubusercontent.com/GboardThemes/Packs/master/flags.json")::getTextFromUrl) {
                if (it.isBlank()) {
                    if (!exists()) {
                        writeText(
                            SafeJSON(
                                JSONObject(resources.openRawResource(
                                    FileUtils.getResourceId(
                                        this@SplashScreen,
                                        "flags",
                                        "raw",
                                        packageName
                                    )
                                ).bufferedReader().use { reader -> reader.readText() })
                            ).toString()
                        )
                    }
                } else {
                    val json = SafeJSON(JSONObject(it))
                    if (!exists() || timeStamp < json.getLong("time"))
                        writeText(json.toString())
                }
            }
        }

        val scheme = intent.scheme
        val data = intent.data

        when {
            scheme != "content" && data != null -> {
                when (data.host?.split(".")?.first()) {
                    "repos" -> {
                        data.queryParameterNames.forEach {
                            when (it) {
                                "add" -> {
                                    PreferenceManager.getDefaultSharedPreferences(this).apply {
                                        val repos = ArrayList(getStringSet("repos", setOf()))
                                        repos.add(data.getQueryParameter("add"))
                                        edit { putStringSet("repos", repos.toSet()) }
                                    }
                                    Toast.makeText(this, R.string.repo_added, Toast.LENGTH_SHORT)
                                        .show()
                                }
                            }
                        }
                    }
                }
                finishAndRemoveTask()
            }
            data != null -> {
                val resultLauncher =
                    registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                        finishAndRemoveTask()
                    }
                val dialog = openLoadingDialog(R.string.unpacking_themes)
                doAsync({
                    val zip = File(cacheDir, "themes.pack").apply {
                        delete()
                        data.writeToFile(this@SplashScreen, this)
                    }
                    if (!zip.exists()) listOf()
                    else {
                        val destination = File(cacheDir, zip.nameWithoutExtension)
                        if (ZipHelper().unpackZip(destination.absolutePath, zip.absolutePath)) {
                            destination.listFiles { file -> file.extension == "zip" }
                                ?.map { it.absolutePath }
                                ?: listOf()
                        } else listOf()
                    }
                }) {
                    dialog.dismiss()
                    resultLauncher.launch(
                        Intent(
                            this,
                            InstallPackActivity::class.java
                        ).putStringArrayListExtra("themes", ArrayList(it))
                    )
                }
            }
            else -> {
                content.viewTreeObserver.addOnPreDrawListener(object :
                    ViewTreeObserver.OnPreDrawListener {
                    override fun onPreDraw(): Boolean {
                        return if (isReady) {
                            content.viewTreeObserver.removeOnPreDrawListener(this)
                            true
                        } else false
                    }
                })

                rootAccess = Shell.rootAccess()
                gboardInstalled = isPackageInstalled(Config.GBOARD_PACKAGE_NAME, packageManager)

                createNotificationChannels()

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
                    else -> checkForUpdate {
                        checkedForUpdate = true
                        startActivity(Intent(this, MainActivity::class.java).putExtra("update", it))
                        finish()
                    }
                }
            }
        }
    }

    private fun checkForUpdate(callback: (update: Boolean) -> Unit) {
        doAsync(URL(checkUpdateUrl)::getTextFromUrl) { text ->
            try {
                val outputMetadata = Gson().fromJson(text, OutputMetadata::class.java)
                val versionCode = outputMetadata.elements.first().versionCode
                callback(versionCode > BuildConfig.VERSION_CODE)
            } catch (e: Exception) {
                callback(false)
            }
        }
    }

    private fun createNotificationChannels() {
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
