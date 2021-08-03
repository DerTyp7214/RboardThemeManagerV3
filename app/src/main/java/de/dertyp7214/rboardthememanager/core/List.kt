package de.dertyp7214.rboardthememanager.core

fun <E> List<E>.containsAny(list: List<E>): Boolean {
    var contains = false
    forEach {
        contains = contains || list.contains(it)
        if (contains) return true
    }
    return contains
}

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