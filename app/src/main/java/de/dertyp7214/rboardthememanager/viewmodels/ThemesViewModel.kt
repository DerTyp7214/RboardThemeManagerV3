package de.dertyp7214.rboardthememanager.viewmodels

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import de.dertyp7214.rboardthememanager.data.ThemeDataClass

class ThemesViewModel : ViewModel() {
    private val themes = MutableLiveData<List<ThemeDataClass>>()
    private val filter = MutableLiveData<String>()

    fun getThemes(): List<ThemeDataClass> {
        return themes.value ?: listOf()
    }

    fun setThemes(list: List<ThemeDataClass>) {
        themes.value = list
    }

    fun themesObserve(owner: LifecycleOwner, observer: Observer<List<ThemeDataClass>>) {
        themes.observe(owner, observer)
    }

    fun getFilter(): String {
        return filter.value ?: ""
    }

    fun setFilter(f: String = "") {
        filter.value = f
    }

    fun observeFilter(owner: LifecycleOwner, observer: Observer<String>) {
        filter.observe(owner, observer)
    }
}