package de.dertyp7214.rboardthememanager.core

import com.topjohnwu.superuser.io.SuFile
import com.topjohnwu.superuser.io.SuFileInputStream
import de.dertyp7214.rboardthememanager.data.ModuleMeta
import java.io.File
import android.app.Activity
import android.content.Intent
import androidx.core.app.ShareCompat
import androidx.core.content.FileProvider
import de.dertyp7214.rboardthememanager.R
import java.io.InputStream
import kotlin.text.Charsets.UTF_8

fun File.parseModuleMeta(): ModuleMeta {
    val text = SuFileInputStream.open(this).readBytes().toString(UTF_8)
    val map = HashMap<String, Any>()
    text.split("\n").forEach {
        val line = it.split("=")
        if (line.size > 1) map[line[0]] = line[1]
    }
    return ModuleMeta(
        (map["id"] ?: "") as String,
        (map["name"] ?: "") as String,
        (map["version"] ?: "") as String,
        (map["versionCode"] ?: "") as String,
        (map["author"] ?: "") as String,
        (map["description"] ?: "") as String,
        map
    )
}

fun File.copyInputStreamToFile(inputStream: InputStream) {
    this.outputStream().use { fileOut ->
        inputStream.copyTo(fileOut)
    }
}

fun File.readXML(string: String? = null): Map<String, Any> {
    val output = HashMap<String, Any>()

    val fileName = absolutePath
    val content = string ?: SuFile(fileName).openStream()?.use {
        it.bufferedReader().readText()
    }

    return content?.readXML() ?: output
}

fun File.share(
    activity: Activity,
    type: String = "*/*",
    action: String = Intent.ACTION_SEND,
    shareText: Int = R.string.share
) {
    val uri = FileProvider.getUriForFile(activity, activity.packageName, this)
    ShareCompat.IntentBuilder(activity)
        .setStream(uri)
        .setType(type)
        .intent.setAction(action)
        .setDataAndType(uri, type)
        .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION).apply {
            activity.startActivity(
                Intent.createChooser(
                    this,
                    activity.getString(shareText)
                )
            )
        }
}