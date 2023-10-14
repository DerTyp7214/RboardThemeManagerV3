@file:Suppress("MemberVisibilityCanBePrivate")

package de.dertyp7214.rboardthememanager.utils

import android.app.Activity
import android.content.Context
import com.jaredrummler.android.shell.Shell
import com.topjohnwu.superuser.io.SuFile
import com.topjohnwu.superuser.io.SuFileInputStream
import com.topjohnwu.superuser.io.SuFileOutputStream
import de.dertyp7214.rboardcomponents.core.preferences
import de.dertyp7214.rboardthememanager.Config
import de.dertyp7214.rboardthememanager.Config.MODULES_PATH
import de.dertyp7214.rboardthememanager.R
import de.dertyp7214.rboardthememanager.core.getString
import de.dertyp7214.rboardthememanager.core.getSystemProp
import de.dertyp7214.rboardthememanager.core.openDialog
import de.dertyp7214.rboardthememanager.core.parseModuleMeta
import de.dertyp7214.rboardthememanager.core.writeFile
import de.dertyp7214.rboardthememanager.data.MagiskModule
import de.dertyp7214.rboardthememanager.data.ModuleMeta
import java.nio.charset.Charset
import com.topjohnwu.superuser.Shell as MShell

@Suppress("unused")
object MagiskUtils {
    fun isMagiskInstalled(): Boolean {
        val result = Shell.run("magisk")
        return result.getStderr().startsWith("magisk", true)
    }

    fun getMagiskVersionString(): String {
        val result = Shell.run("magisk -v")
        return result.getStdout()
    }

    fun getMagiskVersionNumber(): String {
        val result = Shell.run("magisk -V")
        return result.getStdout()
    }

    fun getMagiskVersionFullString(): String {
        val result = Shell.run("magisk -c")
        return result.getStdout()
    }

    fun getModules(): List<MagiskModule> {
        return if (isMagiskInstalled()) {
            SuFile(MODULES_PATH).listFiles()?.filter { SuFile(it, "module.prop").exists() }?.map {
                val meta = SuFile(it, "module.prop").parseModuleMeta()
                MagiskModule(meta.id, it, meta)
            } ?: ArrayList()
        } else ArrayList()
    }

    fun getModule(moduleId: String): MagiskModule? {
        return if (isMagiskInstalled()) {
            val modulePath = SuFile(MODULES_PATH, moduleId)
            SuFile(modulePath, "module.prop").let {
                if (it.exists()) {
                    val meta = it.parseModuleMeta()
                    MagiskModule(meta.id, modulePath, meta)
                } else null
            }
        } else null
    }

    fun isModuleInstalled(moduleId: String): Boolean {
        return getModule(moduleId)?.getSystemProp() != null
    }

    private fun installModule(meta: ModuleMeta, files: Map<String, String?>) {
        val moduleDir = SuFile(MODULES_PATH, meta.id)
        moduleDir.mkdirs()
        writeSuFile(SuFile(moduleDir, "module.prop"), meta.getString())
        files.forEach {
            SuFile(moduleDir, it.key).apply {
                if (it.value != null) writeSuFile(this, it.value ?: "")
                else mkdirs()
            }
        }
    }

    fun installModule(activity: Activity) {
        val files = mapOf(
            Pair(
                "system.prop",
                "# Default Theme and Theme-location\n" +
                        "ro.com.google.ime.themes_dir=${Config.THEME_LOCATION}"
            ),
            Pair(Config.THEME_LOCATION, null)
        )
        installModule(Config.MODULE_META, files)
        activity.openDialog(R.string.reboot_to_continue, R.string.reboot, false, {
            activity.finishAndRemoveTask()
        }) {
            RootUtils.reboot()
        }
    }

    fun uninstallModule(moduleId: String) {
        val moduleDir = SuFile(MODULES_PATH, moduleId)
        if (moduleDir.exists())
            SuFile(moduleDir, "remove").writeFile("")
    }

    fun updateModule(meta: ModuleMeta, files: Map<String, String?>) {
        val moduleDir = SuFile(MODULES_PATH, meta.id)
        moduleDir.mkdirs()
        writeSuFile(
            SuFile(moduleDir, "module.prop").apply { deleteRecursive() },
            meta.getString()
        )
        files.forEach { file ->
            if (SuFile(moduleDir, file.key).exists()) {
                SuFile(moduleDir, file.key).apply {
                    var text = SuFileInputStream.open(this).bufferedReader().readText()

                    if (file.value?.split("=")?.get(0).toString() in text) {
                        text = text.replace(
                            text.lines()
                                .firstOrNull {
                                    file.value?.split("=")?.get(0).toString() in it
                                } ?: "",
                            file.value?.let { if (it.endsWith("=")) "" else it } ?: ""
                        )
                    } else {
                        text += "\n${file.value ?: ""}"
                    }

                    if (text.lines().isEmpty()) {
                        text = file.value ?: ""
                    }

                    SuFile(moduleDir, file.key).apply {
                        deleteRecursive()
                        writeSuFile(this, text)
                    }
                }
            } else {
                SuFile(moduleDir, file.key).apply {
                    if (file.value != null) writeSuFile(this, file.value ?: "")
                    else mkdirs()
                }
            }
        }
    }

    fun Context.buildShell() {
        MShell.enableVerboseLogging = preferences.getString("logMode", "VERBOSE") == "VERBOSE"
        if (MShell.getCachedShell() == null) MShell.setDefaultBuilder(
            MShell.Builder.create().apply {
                setFlags(MShell.FLAG_MOUNT_MASTER)
            })
    }

    private fun writeSuFile(file: SuFile, content: String) {
        SuFileOutputStream.open(file).writer(Charset.defaultCharset())
            .use { outputStreamWriter ->
                outputStreamWriter.write(content)
            }
    }
}