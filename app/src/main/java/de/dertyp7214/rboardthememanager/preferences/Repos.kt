package de.dertyp7214.rboardthememanager.preferences

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.RecyclerView
import de.Maxr1998.modernpreferences.PreferenceScreen
import de.Maxr1998.modernpreferences.PreferencesAdapter
import de.Maxr1998.modernpreferences.helpers.checkBox
import de.Maxr1998.modernpreferences.helpers.onClick
import de.dertyp7214.rboardthememanager.Config
import de.dertyp7214.rboardthememanager.R
import de.dertyp7214.rboardthememanager.core.*
import de.dertyp7214.rboardthememanager.screens.ManageRepo
import de.dertyp7214.rboardthememanager.utils.doAsync
import org.json.JSONArray
import java.net.URL

class Repos(
    private val activity: AppCompatActivity,
    private val args: SafeJSON,
    private val onRequestReload: () -> Unit
) :
    AbstractMenuPreference() {

    private val sharedPreferences by lazy { PreferenceManager.getDefaultSharedPreferences(activity) }
    private val repositories = hashMapOf<String, Boolean>()
    private val resultLauncher: ActivityResultLauncher<Intent>

    private var modified = false

    init {
        val repos = sharedPreferences.getStringSet("repos", Config.REPOS.toSet())

        repos?.let { repositories.putAll(it.toList().toMap()) }

        resultLauncher =
            activity.registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                if (it.resultCode == RESULT_OK) {
                    it.data?.getStringExtra("key")?.also { key ->
                        when (it.data?.getStringExtra("action")) {
                            "delete" -> {
                                modified = true
                                repositories.remove(key)
                                onRequestReload()
                            }
                            "disable" -> {
                                modified = true
                                repositories[key] = false
                                onRequestReload()
                            }
                            "enable" -> {
                                modified = true
                                repositories[key] = true
                                onRequestReload()
                            }
                            "share" -> {
                                TODO("Add share logic for repo")
                            }
                        }
                    }
                }
            }
    }

    override fun preferences(builder: PreferenceScreen.Builder) {
        val prefs = PreferenceManager.getDefaultSharedPreferences(activity)
        repositories.forEach { (key, value) ->
            prefs.edit { remove(key) }
            builder.checkBox(key) {
                val pref = this
                title = key.replace("https://raw.githubusercontent.com/", "...")
                defaultValue = value
                onClick {
                    pref.checked = value
                    ManageRepo::class.java.start(activity, resultLauncher) {
                        putExtra("key", key)
                        putExtra("enabled", value)
                    }
                    false
                }
            }
        }
    }

    override fun loadMenu(menuInflater: MenuInflater, menu: Menu?) {
        if (menu != null) menuInflater.inflate(R.menu.repos, menu)
    }

    override fun onMenuClick(menuItem: MenuItem): Boolean {
        return when (menuItem.itemId) {
            R.id.add -> {
                activity.openInputDialog(R.string.repository) { dialog, text ->
                    doAsync({
                        try {
                            JSONArray(URL(text).getTextFromUrl())
                            true
                        } catch (e: Exception) {
                            false
                        }
                    }) {
                        dialog.dismiss()
                        if (it) {
                            repositories[text] = true
                            onRequestReload()
                            modified = true
                        } else activity.openDialog(
                            R.string.invalid_repo_long,
                            R.string.invalid_repo,
                            false,
                            ::dismiss,
                            ::dismiss
                        )
                    }
                }
                true
            }
            R.id.apply -> {
                apply()
                modified = false
                true
            }
            else -> false
        }
    }

    override fun getExtraView(): View? = null

    override fun onStart(recyclerView: RecyclerView, adapter: PreferencesAdapter) {
        adapter.currentScreen.indexOf(args.getString("highlight"))
            .let { if (it >= 0) recyclerView.scrollToPosition(it) }
    }

    private fun apply() {
        sharedPreferences.edit {
            putStringSet("repos", repositories.toSet())
        }
        Config.REPOS.apply {
            clear()
            addAll(repositories.toSet())
        }
    }

    override fun onBackPressed(callback: () -> Unit) {
        if (modified) {
            activity.openDialog(R.string.save_repos, R.string.apply, false, {
                callback()
            }) {
                apply()
                callback()
            }
        } else callback()
    }
}