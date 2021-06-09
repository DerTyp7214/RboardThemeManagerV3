package de.dertyp7214.rboardthememanager.screens

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import de.Maxr1998.modernpreferences.PreferencesAdapter
import de.dertyp7214.rboardthememanager.R
import de.dertyp7214.rboardthememanager.utils.Preferences
import de.dertyp7214.rboardthememanager.utils.doAsync
import dev.chrisbanes.insetter.applyInsetter
import kotlinx.android.synthetic.main.activity_preferences.*

class PreferencesActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setDecorFitsSystemWindows(false)
        setContentView(R.layout.activity_preferences)

        val preferences = Preferences(this, intent)

        setSupportActionBar(preferences_toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        title = preferences.title

        preferences_toolbar.applyInsetter {
            type(statusBars = true) {
                margin()
            }
        }

        recyclerView.layoutManager = LinearLayoutManager(this)
        doAsync({ PreferencesAdapter(preferences.preferences) }) {
            loadingPreferences.visibility = View.GONE
            recyclerView.adapter = it
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}