package de.dertyp7214.rboardthememanager.data

data class ThemePack(
    val author: String,
    val url: String,
    val title: String,
    val tags: List<String>,
    val description: String? = null,
    val none: Boolean = false
) {
    companion object {
        val NONE = ThemePack("", "", "", listOf(), null, true)
    }
}