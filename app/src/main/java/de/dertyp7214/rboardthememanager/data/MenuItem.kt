package de.dertyp7214.rboardthememanager.data

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

data class MenuItem(
    @param:DrawableRes val icon: Int,
    @param:StringRes val title: Int,
    val visible: Boolean = true,
    val onClick: () -> Unit
)