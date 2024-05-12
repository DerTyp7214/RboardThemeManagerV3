@file:Suppress("DEPRECATION")

package de.dertyp7214.rboardthememanager.screens

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import de.Maxr1998.modernpreferences.PreferencesAdapter
import de.dertyp7214.rboardcomponents.utils.doAsync
import de.dertyp7214.rboardthememanager.core.addCallback
import de.dertyp7214.rboardthememanager.databinding.ActivityPreferencesBinding
import de.dertyp7214.rboardthememanager.preferences.Preferences
import dev.chrisbanes.insetter.applyInsetter

class PreferencesActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPreferencesBinding
    private lateinit var preferences: Preferences
    private lateinit var recyclerView: RecyclerView

    private val callbacks: ArrayList<OnBackPressedCallback> = arrayListOf()

    @Suppress("MemberVisibilityCanBePrivate")
    companion object {
        private val instances = arrayListOf<PreferencesActivity>()

        fun clearInstances() {
            while (instances.isNotEmpty()) popInstance()
        }

        fun popInstance() {
            if (Build.VERSION.SDK_INT >= 35) {
                instances.removeLast().finish()
            } else {
                instances.removeLastOrNull()?.finish()
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPreferencesBinding.inflate(layoutInflater)
        window.setDecorFitsSystemWindows(false)
        setContentView(binding.root)

        instances.add(this)

        val preferencesToolbar = binding.preferencesToolbar
        val loadingPreferences = binding.loadingPreferences
        val extraContent = binding.extraContent
        recyclerView = binding.recyclerView

        preferences = Preferences(this, intent) {
            recyclerView.adapter.let { adapter ->
                if (adapter is PreferencesAdapter)
                    adapter.setRootScreen(preferences.preferences)
            }
        }

        preferences.extraView?.let { extraContent.addView(it) }

        preferences.handleFab(binding.floatingActionButton)

        binding.floatingActionButton.applyInsetter {
            type(navigationBars = true) {
                margin()
            }
        }

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
            preferences.onStart(recyclerView, it)
        }

        callbacks.forEach { it.remove() }
        onBackPressedDispatcher.addCallback(this, true, callbacks::add) {
            preferences.onBackPressed {
                isEnabled = false
                onBackPressedDispatcher.onBackPressed()
                isEnabled = true
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        preferences.loadMenu(menuInflater, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return preferences.onMenuClick(item)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }

    override fun onDestroy() {
        callbacks.forEach { it.remove() }
        instances.remove(this)
        super.onDestroy()
    }
}