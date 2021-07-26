package de.dertyp7214.rboardthememanager.utils

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import de.dertyp7214.rboardthememanager.Config.SOUNDS_PACKS_URL
import de.dertyp7214.rboardthememanager.data.SoundPack
import java.net.URL

object SoundHelper {
    fun loadSoundPacks(): List<SoundPack> {
        return try {
            Gson().fromJson(
                URL(SOUNDS_PACKS_URL).readText(),
                object : TypeToken<List<SoundPack>>() {}.type
            )
        } catch (e: Exception) {
            listOf()
        }
    }
}