package de.dertyp7214.rboardthememanager.data

data class SoundPack(
    val author: String, val url: String, val title: String,
    val size: Long = 0,
    val date: Long = Long.MAX_VALUE
) {
    override fun hashCode(): Int {
        return author.hashCode() + url.hashCode()
    }
    override fun equals(other: Any?): Boolean {

        other as SoundPack

        if (author != other.author) return false
        if (url != other.url) return false
        if (size != other.size) return false
        if (date != other.date) return false

        return true
    }}