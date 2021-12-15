package de.dertyp7214.rboardthememanager.utils

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

fun <T> doAsync(doInBackground: () -> T, getResult: (result: T) -> Unit) {
    CoroutineScope(Dispatchers.Main).launch {
        withContext(Dispatchers.Default) {
            val result = doInBackground()
            CoroutineScope(Dispatchers.Main).launch {
                getResult(result)
            }
        }
    }
}
fun doInBackground(doInBackground: () -> Unit) {
    CoroutineScope(Dispatchers.Main).launch {
        withContext(Dispatchers.Default) { doInBackground() }
    }
}
infix fun <T> (() -> T).asyncInto(into: (result: T) -> Unit) = doAsync(this, into)