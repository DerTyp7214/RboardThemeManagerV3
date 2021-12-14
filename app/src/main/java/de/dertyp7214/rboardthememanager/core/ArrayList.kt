package de.dertyp7214.rboardthememanager.core

import de.dertyp7214.rboardthememanager.data.RboardRepo

fun <T> ArrayList<T>.setAll(t: T) {
    for (i in 0 until size) set(i, t)
}

fun ArrayList<String>.filterActive(): List<String> {
    return toMap().filter { it.value }.map { it.key }
}

fun ArrayList<RboardRepo>.remove(url: String) = remove(find { it.url == url })

operator fun ArrayList<RboardRepo>.get(url: String) = find { it.url == url }