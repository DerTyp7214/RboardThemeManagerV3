package de.dertyp7214.rboardthememanager.core

import de.dertyp7214.rboardthememanager.data.RboardRepo

fun ArrayList<RboardRepo>.toStringSet(): Set<String> {
    return map {
        "${it.active.toString().lowercase()}:${it.url}"
    }.toSet()
}