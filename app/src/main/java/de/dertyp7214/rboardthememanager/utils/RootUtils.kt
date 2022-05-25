package de.dertyp7214.rboardthememanager.utils

import de.dertyp7214.rboardthememanager.core.runAsCommand

object RootUtils {
    fun reboot() = "svc power reboot || reboot".runAsCommand()
}