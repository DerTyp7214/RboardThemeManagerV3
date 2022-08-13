package de.dertyp7214.rboardthememanager.core

import de.dertyp7214.rboardthememanager.data.ThemePack
import de.dertyp7214.rboardthememanager.proto.My

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

fun List<My.Object>.toPackList() = map {
    ThemePack(
        it.author,
        it.url,
        it.name,
        it.tagsList,
        it.themesList,
        it.size,
        it.description,
        false,
        it.date
    )
}