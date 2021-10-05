package de.dertyp7214.rboardthememanager.components

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MarginItemDecoration(
    private val spaceSize: Int,
    private val top: Boolean = true,
    private val bottom: Boolean = true,
    private val left: Boolean = false,
    private val right: Boolean = false,
    private val all: Boolean = false
) : RecyclerView.ItemDecoration() {
    override fun getItemOffsets(
        outRect: Rect, view: View,
        parent: RecyclerView, state: RecyclerView.State
    ) {
        with(outRect) {
            if (all || (this@MarginItemDecoration.top && parent.getChildAdapterPosition(view) != 0)) top =
                spaceSize
            if (all || this@MarginItemDecoration.left) left = spaceSize
            if (all || this@MarginItemDecoration.right) right = spaceSize
            if (all || (this@MarginItemDecoration.bottom && parent.getChildAdapterPosition(view) != (parent.adapter?.itemCount?.minus(
                    1
                ) ?: 0))
            ) bottom = spaceSize
        }
    }
}