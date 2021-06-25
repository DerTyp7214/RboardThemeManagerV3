package de.dertyp7214.rboardthememanager.data

data class ThemePack(
    val author: String,
    val url: String,
    val title: String,
    val tags: List<String>,
    val none: Boolean = false
) {
    companion object {
        val NONE = ThemePack("", "", "", listOf(), true)
    }
}