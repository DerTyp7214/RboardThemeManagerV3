package de.dertyp7214.rboardthememanager.core

operator fun <E> List<E>.times(times: Int): ArrayList<E> {
    val list = ArrayList(this)
    for (i in 0..times) list.addAll(this)
    return list
}

inline fun <E> List<E>.forEachIt(action: (E) -> Unit) = iterator().forEach(action)

inline fun <E> List<E>.forEachIndexedIt(action: (Int, E) -> Unit) =
    iterator().withIndex().forEach { action(it.index, it.value) }

inline fun <E> List<E>.filterIt(to: ArrayList<E> = ArrayList(), predicate: (E) -> Boolean) =
    to.also { list ->
        forEachIt { if (predicate(it)) list.add(it) }
    }

fun List<String>.toMap(): Map<String, Boolean> {
    return associate {
        val bool = it.startsWith("true:")
        val str = it.removePrefix("true:").removePrefix("false:")
        Pair(str, bool)
    }
}