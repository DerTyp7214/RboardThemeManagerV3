package de.dertyp7214.rboardthememanager.data

import com.google.gson.annotations.SerializedName

data class ThemePack(
    val author: String,
    val url: String,
    @SerializedName(value = "title", alternate = ["name"])
    val name: String,
    val tags: List<String>,
    val description: String? = null,
    val none: Boolean = false,
    val date: Long = Long.MAX_VALUE
) {

    companion object {
        val NONE = ThemePack("", "", "", listOf(), null, true)
    }
}