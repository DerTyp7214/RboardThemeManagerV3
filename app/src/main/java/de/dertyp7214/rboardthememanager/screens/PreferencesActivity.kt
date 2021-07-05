package de.dertyp7214.rboardthememanager.screens

import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.recyclerview.widget.LinearLayoutManager
import de.Maxr1998.modernpreferences.PreferencesAdapter
import de.dertyp7214.rboardthememanager.core.runAsCommand
import de.dertyp7214.rboardthememanager.databinding.ActivityPreferencesBinding
import de.dertyp7214.rboardthememanager.utils.Preferences
import de.dertyp7214.rboardthememanager.utils.doAsync
import dev.chrisbanes.insetter.applyInsetter

class PreferencesActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPreferencesBinding
    private lateinit var preferences: Preferences

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPreferencesBinding.inflate(layoutInflater)
        window.run{
            WindowCompat.setDecorFitsSystemWindows(this, false)
        }
        setContentView(binding.root)

        val rebootResultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                "reboot".runAsCommand()
            }

        preferences = Preferences(this, intent)
        preferences.putExtra("rebootLauncher", rebootResultLauncher)

        val preferencesToolbar = binding.preferencesToolbar
        val loadingPreferences = binding.loadingPreferences
        val recyclerView = binding.recyclerView


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