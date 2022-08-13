package de.dertyp7214.rboardthememanager.core

import de.dertyp7214.rboardthememanager.data.ThemePack
import de.dertyp7214.rboardthememanager.proto.ThemePackList

operator fun <E> List<E>.times(times: Int): ArrayList<E> {
    val list = ArrayList(this)
    for (i in 0..times) list.addAll(this)
    return list
}

fun List<String>.toMap(): Map<String, Boolean> {
    return associate {
        val bool = it.startsWith("true:")
        val str = it.removePrefix("true:").removePrefix("false:")
        Pair(str, bool)
    }
}

fun List<ThemePackList.Object>.toPackList() = map {
    ThemePack(
        it.getAuthor(),
        it.getUrl(),
        it.getName(),
        it.getTagsList() ?: listOf(),
        it.getThemesList(),
        it.getSize(),
        it.getDescription(),
        false,
        it.getDate()
    )
}