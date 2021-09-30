@file:Suppress("unused")

package de.dertyp7214.rboardthememanager.components

import android.annotation.SuppressLint
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.view.WindowInsets
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.MenuRes
import androidx.appcompat.widget.PopupMenu
import androidx.cardview.widget.CardView
import androidx.core.widget.doOnTextChanged
import de.dertyp7214.rboardthememanager.R

@SuppressLint("ResourceType")
class SearchBar(context: Context, attrs: AttributeSet? = null) : LinearLayout(context, attrs) {

    var focus = false
        private set
    private var searchListener: (text: String) -> Unit = {}
    private var closeListener: () -> Unit = {}
    private var focusListener: () -> Unit = {}
    private var menuListener: (ImageButton) -> Unit = {}

    private var popupMenu: PopupMenu? = null
    private var menuItemClickListener: PopupMenu.OnMenuItemClickListener? = null

    private val searchBar: CardView
    private val searchButton: ImageButton
    private val backButton: ImageButton
    private val moreButton: ImageButton
    private val searchText: TextView
    private val searchEdit: EditText

    var instantSearch: Boolean = false

    var text: String = ""
        set(value) {
            field = value
            if (value.isEmpty()) {
                focus = false
                searchButton.visibility = VISIBLE
                backButton.visibility = GONE

                searchText.visibility = VISIBLE
                searchEdit.visibility = GONE
            }

            searchEdit.setText(value)
            clearFocus(searchEdit)
        }

    var menuVisible: Boolean = true
        set(value) {
            field = value
            if (value) searchButton.setImageResource(R.drawable.ic_hamburger)
            else searchButton.setImageResource(R.drawable.ic_baseline_search_24)
        }

    init {
        inflate(context, R.layout.search_bar, this)

        searchBar = findViewById(R.id.search_bar)

        searchButton = findViewById(R.id.search_button)
        backButton = findViewById(R.id.back_button)
        moreButton = findViewById(R.id.more_button)

        searchText = findViewById(R.id.search_text)
        searchEdit = findViewById(R.id.search)

        moreButton.visibility = INVISIBLE

        searchBar.setOnClickListener {
            if (!focus) {
                focus = true
                searchButton.visibility = GONE
                backButton.visibility = VISIBLE

                searchText.visibility = GONE
                searchEdit.visibility = VISIBLE

                searchEdit.windowInsetsController?.show(WindowInsets.Type.ime())
                searchEdit.requestFocus()
                focusListener()
            }
        }

        backButton.setOnClickListener {
            if (focus) {
                focus = false
                searchButton.visibility = VISIBLE
                backButton.visibility = GONE

                searchText.visibility = VISIBLE
                searchEdit.visibility = GONE

                searchEdit.setText("")
                clearFocus(searchEdit)
                closeListener()
            }
        }

        moreButton.setOnClickListener {
            popupMenu?.show()
        }

        searchEdit.doOnTextChanged { text, _, _, _ ->
            if (instantSearch) searchListener(text?.toString() ?: "")
        }

        searchEdit.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                clearFocus(searchEdit)
                searchListener(searchEdit.text.toString())
                true
            } else false
        }

        searchButton.setOnClickListener {
            if (menuVisible) menuListener(searchButton)
            else searchBar.callOnClick()
        }
    }

    fun setMenu(
        @MenuRes menu: Int? = null,
        itemClickListener: PopupMenu.OnMenuItemClickListener? = null
    ) {
        popupMenu = if (menu != null) {
            moreButton.visibility = VISIBLE
            PopupMenu(context, moreButton).also { popup ->
                popup.menuInflater.inflate(menu, popup.menu)
                popup.setOnMenuItemClickListener(itemClickListener)
            }
        }
        else {
            moreButton.visibility = INVISIBLE
            null
        }
    }

    fun clearText() = ::text.set("")

    fun setOnSearchListener(listener: (text: String) -> Unit) {
        searchListener = listener
    }

    fun setOnCloseListener(listener: () -> Unit) {
        closeListener = listener
    }

    fun setOnFocusListener(listener: () -> Unit) {
        focusListener = listener
    }

    fun setOnMenuListener(listener: (ImageButton) -> Unit) {
        menuListener = listener
    }

    override fun clearFocus() {
        clearFocus(searchEdit)
    }

    private fun clearFocus(editText: EditText) {
        Handler(Looper.getMainLooper()).postDelayed({
            editText.windowInsetsController?.hide(WindowInsets.Type.ime())
        }, 100)
    }
}