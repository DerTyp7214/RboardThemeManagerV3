package de.dertyp7214.rboardthememanager.screens

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import de.Maxr1998.modernpreferences.PreferencesAdapter
import de.dertyp7214.rboardthememanager.databinding.ActivityPreferencesBinding
import de.dertyp7214.rboardthememanager.preferences.Preferences
import de.dertyp7214.rboardthememanager.utils.doAsync
import dev.chrisbanes.insetter.applyInsetter

class PreferencesActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPreferencesBinding
    private lateinit var preferences: Preferences

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPreferencesBinding.inflate(layoutInflater)
        window.setDecorFitsSystemWindows(false)
        setContentView(binding.root)

        val preferencesToolbar = binding.preferencesToolbar
        val loadingPreferences = binding.loadingPreferences
        val recyclerView = binding.recyclerView
        val extraContent = binding.extraContent

        preferences = Preferences(this, intent) {
            recyclerView.adapter.let { adapter ->
                if (adapter is PreferencesAdapter)
                    adapter.setRootScreen(preferences.preferences)
            }
        }

        preferences.extraView?.let { extraContent.addView(it) }

        setSupportActionBar(preferencesToolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        title = preferences.title

        preferencesToolbar.applyInsetter {
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

    override fun onBackPressed() {
        super.onBackPressed()
        preferences.onBackPressed()
    }
}