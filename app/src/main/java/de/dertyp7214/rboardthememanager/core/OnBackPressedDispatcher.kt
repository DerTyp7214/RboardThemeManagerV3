package de.dertyp7214.rboardthememanager.core

import androidx.activity.OnBackPressedCallback
import androidx.activity.OnBackPressedDispatcher
import androidx.lifecycle.LifecycleOwner

fun OnBackPressedDispatcher.addCallback(lifecycleOwner: LifecycleOwner, enabled: Boolean, callback: (OnBackPressedCallback) -> Unit, body: OnBackPressedCallback.() -> Unit) {
    val backPressedCallback = object : OnBackPressedCallback(enabled) {
        override fun handleOnBackPressed() {
            body()
        }
    }
    callback(backPressedCallback)
    addCallback(lifecycleOwner, backPressedCallback)
}