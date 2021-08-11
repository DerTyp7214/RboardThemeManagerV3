package de.dertyp7214.rboardthememanager.screens

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
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
    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPreferencesBinding.inflate(layoutInflater)
        window.run {
            WindowCompat.setDecorFitsSystemWindows(this, false)
        }
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

        recyclerView.applyInsetter {
            type(navigationBars = true) {
                margin()
            }
        }


        recyclerView.layoutManager = LinearLayoutManager(this)
        doAsync({ PreferencesAdapter(preferences.preferences) }) {
            loadingPreferences.visibility = View.GONE
            recyclerView.adapter = it
        }
    }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        preferences.loadMenu(menuInflater, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return preferences.onMenuClick(item)
    }


    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onBackPressed() {
        preferences.onBackPressed { super.onBackPressed() }
    }
}