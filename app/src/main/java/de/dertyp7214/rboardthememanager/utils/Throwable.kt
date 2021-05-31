package de.dertyp7214.rboardthememanager.utils

interface Throwable<E> {
    fun catch(callback: (error: String) -> E): Finally<E>
}

interface Finally<E> {
    fun finally(run: () -> E): E
}

fun <E> newThrowable(): Triple<Throwable<E>, ((error: String) -> E)?, (() -> E) -> E> {
    var callbackFun: ((error: String) -> E)? = null
    val f: (() -> E) -> E = { it() }
    val throwable = object : Throwable<E> {
        override fun catch(callback: (error: String) -> E): Finally<E> {
            callbackFun = callback
            return object : Finally<E> {
                override fun finally(run: () -> E): E {
                    return f(run)
                }
            }
        }
    }
    return Triple(throwable, callbackFun, f)
}