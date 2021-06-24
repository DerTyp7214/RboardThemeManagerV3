package de.dertyp7214.rboardthememanager.core

import androidx.fragment.app.Fragment
import com.google.android.material.transition.MaterialSharedAxis

fun Fragment.applyTransitions() {
    enterTransition = MaterialSharedAxis(MaterialSharedAxis.Z, true)
    returnTransition = MaterialSharedAxis(MaterialSharedAxis.Z, false)
    exitTransition = MaterialSharedAxis(MaterialSharedAxis.Z, true)
    reenterTransition = MaterialSharedAxis(MaterialSharedAxis.Z, false)
}

fun Fragment.applyTransitionsViewCreated() {
    view?.setBackgroundResource(android.R.color.transparent)
}