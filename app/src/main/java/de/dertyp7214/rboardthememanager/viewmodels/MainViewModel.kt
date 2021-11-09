package de.dertyp7214.rboardthememanager.viewmodels

import androidx.annotation.IdRes
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import de.dertyp7214.rboardthememanager.adapter.ThemeAdapter
import de.dertyp7214.rboardthememanager.data.SoundPack
import de.dertyp7214.rboardthememanager.data.ThemeDataClass
import de.dertyp7214.rboardthememanager.data.ThemePack

@Suppress("unused")
class MainViewModel : ViewModel() {
    private val selectedTheme = MutableLiveData<ThemeDataClass?>()
    private val themePacks = MutableLiveData<List<ThemePack>>()
    private val themes = MutableLiveData<List<ThemeDataClass>>()
    private val sounds = MutableLiveData<List<SoundPack>>()
    private val filter = MutableLiveData<String>()
    private val clearSearch = MutableLiveData<String>()
    private val refreshThemes = MutableLiveData<String>()
    private val selections = MutableLiveData<Pair<Boolean, ThemeAdapter?>>()
    private val navigate = MutableLiveData<Int>()
    private val loaded = MutableLiveData<Boolean>()
    private val packsSortByDate = MutableLiveData<Boolean>()

    fun getSelectedTheme(): ThemeDataClass? {
        return selectedTheme.value
    }

    fun setSelectedTheme(theme: ThemeDataClass? = null) {
        selectedTheme.value = theme
    }

    fun observerSelectedTheme(owner: LifecycleOwner, observer: Observer<ThemeDataClass?>) {
        selectedTheme.observe(owner, observer)
    }

    fun getThemePacks(): List<ThemePack> {
        return themePacks.value ?: listOf()
    }

    fun setThemePacks(list: List<ThemePack> = listOf()) {
        themePacks.value = list
    }

    fun themePacksObserve(owner: LifecycleOwner, observer: Observer<List<ThemePack>>) {
        themePacks.observe(owner, observer)
    }

    fun getSounds(): List<SoundPack> {
        return sounds.value ?: listOf()
    }

    fun setSounds(list: List<SoundPack> = listOf()) {
        sounds.value = list
    }

    fun soundsObserve(owner: LifecycleOwner, observer: Observer<List<SoundPack>>) {
        sounds.observe(owner, observer)
    }

    fun getSelections(): Pair<Boolean, ThemeAdapter?> {
        return selections.value ?: Pair(false, null)
    }

    fun setSelections(value: Pair<Boolean, ThemeAdapter?> = Pair(false, null)) {
        selections.value = value
    }

    fun observeSelections(owner: LifecycleOwner, observer: Observer<Pair<Boolean, ThemeAdapter?>>) {
        selections.observe(owner, observer)
    }

    fun getThemes(): List<ThemeDataClass> {
        return themes.value ?: listOf()
    }

    fun setThemes(list: List<ThemeDataClass> = listOf()) {
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

    fun clearSearch() {
        clearSearch.value = ""
    }

    fun onClearSearch(owner: LifecycleOwner, observer: Observer<String>) {
        clearSearch.observe(owner, observer)
    }

    fun refreshThemes() {
        refreshThemes.value = ""
    }

    fun onRefreshThemes(owner: LifecycleOwner, observer: Observer<String>) {
        refreshThemes.observe(owner, observer)
    }

    fun navigate(@IdRes id: Int) {
        navigate.value = id
    }

    fun onNavigate(owner: LifecycleOwner, observer: Observer<Int>) {
        navigate.observe(owner, observer)
    }

    fun loaded(): Boolean {
        return loaded.value == true
    }

    fun setLoaded(loaded: Boolean) {
        this.loaded.value = loaded
    }

    fun observeLoaded(owner: LifecycleOwner, observer: Observer<Boolean>) {
        loaded.observe(owner, observer)
    }

    fun packsSortByDate(): Boolean {
        return packsSortByDate.value ?: false
    }

    fun setPacksSortByDate(value: Boolean) {
        packsSortByDate.value = value
    }

    fun observePacksSortByDate(owner: LifecycleOwner, observer: Observer<Boolean>) {
        packsSortByDate.observe(owner, observer)
    }
}