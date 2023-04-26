package de.dertyp7214.rboardthememanager.data

import com.google.gson.annotations.SerializedName
import de.dertyp7214.rboardthememanager.core.RepoUrl

data class ThemePack(
    val author: String,
    val url: String,
    val hash: String? = null,
    @SerializedName(value = "title", alternate = ["name"])
    val name: String,
    val tags: List<String>,
    val themes: List<String>? = listOf(),
    val size: Long = 0,
    val description: String? = null,
    val none: Boolean = false,
    val date: Long = Long.MAX_VALUE
) {

    var repoUrl: RepoUrl = ""

    override fun hashCode(): Int {
        return author.hashCode() + url.hashCode() + name.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ThemePack

        if (author != other.author) return false
        if (url != other.url) return false
        if (hash != other.hash) return false
        if (name != other.name) return false
        if (tags != other.tags) return false
        if (themes != other.themes) return false
        if (size != other.size) return false
        if (description != other.description) return false
        if (none != other.none) return false
        if (date != other.date) return false

        return true
    }

    companion object {
        val NONE = ThemePack("", "", null, "", listOf(), listOf(), 0, null, true)    }
}