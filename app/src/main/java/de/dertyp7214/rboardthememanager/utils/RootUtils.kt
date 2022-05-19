package de.dertyp7214.rboardthememanager.utils

import com.topjohnwu.superuser.Shell
import de.dertyp7214.rboardthememanager.core.runAsCommand

@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.FUNCTION)
annotation class RunWithRoot

object RootUtils {
    @RunWithRoot
    fun <E> runWithRoot(run: () -> E): Throwable<E> {
        val triple = newThrowable<E>()
        val throwable = triple.first
        val callback = triple.second
        triple.third
        if (Shell.isAppGrantedRoot() == true) {
            run()
        } else {
            callback?.invoke("No root access!")
        }
        return throwable
    }

    fun reboot() = "svc power reboot ||  reboot".runAsCommand()
}