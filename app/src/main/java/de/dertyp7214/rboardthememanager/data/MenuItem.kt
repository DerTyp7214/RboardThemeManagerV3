package de.dertyp7214.rboardthememanager.data

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

data class MenuItem(@DrawableRes val icon: Int, @StringRes val title: Int, val onClick: () -> Unit)