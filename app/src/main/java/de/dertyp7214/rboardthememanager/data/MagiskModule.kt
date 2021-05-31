package de.dertyp7214.rboardthememanager.data

import java.io.File

class MagiskModule(val id: String, val path: File, val meta: ModuleMeta)

class ModuleMeta(val id: String, val name: String, val version: String, val versionCode: String, val author: String, val description: String, val raw: Map<String, Any> = mapOf())