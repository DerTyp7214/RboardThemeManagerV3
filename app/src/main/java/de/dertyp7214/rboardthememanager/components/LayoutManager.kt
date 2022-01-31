package de.dertyp7214.rboardthememanager.components

import android.content.Context
import androidx.recyclerview.widget.GridLayoutManager

class LayoutManager(context: Context, spanCount: Int = 1) : GridLayoutManager(context, spanCount) {
    override fun supportsPredictiveItemAnimations(): Boolean = true
}