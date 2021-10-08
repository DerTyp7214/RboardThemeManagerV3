package de.dertyp7214.rboardthememanager.core

import com.topjohnwu.superuser.io.SuFile
import com.topjohnwu.superuser.io.SuFileInputStream
import de.dertyp7214.rboardthememanager.data.MagiskModule
import de.dertyp7214.rboardthememanager.data.ModuleMeta

fun ModuleMeta.getString(): String {
    return "id=$id\nname=$name\nversion=$version\nversionCode=$versionCode\nauthor=$author\ndescription=$description"
}

fun MagiskModule.getSystemProp(): String? {
    return try {
        val propsFile = SuFile(path, "system.prop")
        if (propsFile.exists()) SuFileInputStream.open(propsFile).bufferedReader().readText()
        else null
    } catch (e: Exception) {
        null
    }?.ifEmpty { null }
}