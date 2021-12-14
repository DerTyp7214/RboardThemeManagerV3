package de.dertyp7214.rboardthememanager.data

data class RboardRepo(
    val url: String,
    var active: Boolean,
    val meta: RboardRepoMeta? = null
)

data class RboardRepoMeta(
    val name: String = ""
)