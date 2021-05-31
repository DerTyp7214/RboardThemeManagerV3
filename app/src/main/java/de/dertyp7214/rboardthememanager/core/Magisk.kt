package de.dertyp7214.rboardthememanager.core

import de.dertyp7214.rboardthememanager.data.ModuleMeta

fun ModuleMeta.getString(): String {
    return "id=$id\nname=$name\nversion=$version\nversionCode=$versionCode\nauthor=$author\ndescription=$description"
}