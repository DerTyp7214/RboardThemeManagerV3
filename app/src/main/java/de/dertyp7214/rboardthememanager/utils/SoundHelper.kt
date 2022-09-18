package de.dertyp7214.rboardthememanager.utils

import com.google.gson.Gson
import com.topjohnwu.superuser.io.SuFile
import de.dertyp7214.rboardthememanager.Config.SOUNDS_PACKS_URL
import de.dertyp7214.rboardthememanager.data.SoundPack
import java.net.URL

object SoundHelper {
    fun loadSoundPacks(): List<SoundPack> {
        return try {
            Gson().fromJson(URL(SOUNDS_PACKS_URL).readText(), TypeTokens<List<SoundPack>>())
        } catch (e: Exception) {
            listOf()
        }
    }
}

fun getSoundsDirectory(): SuFile? {
    val productMedia = SuFile("/system/product/media/audio/ui/KeypressStandard.ogg")
    val systemMedia = SuFile("/system/media/audio/ui/KeypressStandard.ogg")
    return if (productMedia.exists() && productMedia.isFile) {
        SuFile("/system/product/media")
    } else if (systemMedia.exists() && systemMedia.isFile) {
        SuFile("/system/media")
    } else {
        null
    }
}