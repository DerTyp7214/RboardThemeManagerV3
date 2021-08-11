package de.dertyp7214.rboardthememanager.core

fun <T> ArrayList<T>.setAll(t: T) {
    for (i in 0 until size) set(i, t)
}

fun ArrayList<String>.filterActive(): List<String> {
    return toMap().filter { it.value }.map { it.key }
}