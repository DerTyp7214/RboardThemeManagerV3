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
import androidx.core.content.ContextCompat
import androidx.core.content.edit
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import de.Maxr1998.modernpreferences.PreferenceScreen
import de.Maxr1998.modernpreferences.PreferencesAdapter
import de.Maxr1998.modernpreferences.helpers.checkBox
import de.Maxr1998.modernpreferences.helpers.onClick
import de.dertyp7214.rboardthememanager.Config
import de.dertyp7214.rboardthememanager.R
import de.dertyp7214.rboardthememanager.core.*
import de.dertyp7214.rboardthememanager.data.RboardRepo
import de.dertyp7214.rboardthememanager.screens.ManageRepo
import de.dertyp7214.rboardcomponents.utils.doAsync

class Repos(
    private val activity: AppCompatActivity,
    private val args: SafeJSON,
    private val onRequestReload: () -> Unit
) :
    AbstractFabPreference() {

    private val sharedPreferences by lazy { PreferenceManager.getDefaultSharedPreferences(activity) }
    private val repositories = arrayListOf<RboardRepo>()
    private val resultLauncher: ActivityResultLauncher<Intent>

    private var modified = false

    init {
        val repos = sharedPreferences.getStringSet("repos", Config.REPOS.toSet())

        repos?.let {
            doAsync({
                it.toList().map { url -> url.parseRepo() }
            }, { data ->
                repositories.addAll(data.filterNotNull())
                onRequestReload()
            })
        }

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
                                repositories[key]?.active = false
                                onRequestReload()
                            }
                            "enable" -> {
                                modified = true
                                repositories[key]?.active = true
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
        repositories.forEach { repo ->
            val key = repo.url
            val active = repo.active
            prefs.edit { remove(key) }
            builder.checkBox(key) {
                val pref = this
                val fallbackName = key.replace("https://raw.githubusercontent.com/", "...")
                title = repo.meta?.name?.ifEmpty { fallbackName } ?: fallbackName
                defaultValue = active
                onClick {
                    pref.checked = active
                    ManageRepo::class.java[activity, resultLauncher] = {
                        putExtra("key", key)
                        putExtra("enabled", active)
                    }
                    false
                }
            }
        }
    }

    override fun loadMenu(menuInflater: MenuInflater, menu: Menu?) {
        if (menu != null) menuInflater.inflate(R.menu.repos, menu)
    }

    override fun handleFab(fab: FloatingActionButton) {
        fab.visibility = View.VISIBLE
        fab.setImageDrawable(ContextCompat.getDrawable(activity, R.drawable.ic_baseline_add_24))
        fab.setOnClickListener {
            activity.openInputDialog(R.string.repository) { dialog, text ->
                doAsync("true:$text"::parseRepo) {
                    dialog.dismiss()
                    if (it != null) {
                        repositories.add(it)
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
        }
    }

    override fun onMenuClick(menuItem: MenuItem): Boolean {
        return when (menuItem.itemId) {
            R.id.help -> {
                activity.openUrl("https://github.com/GboardThemes/RepoTemplate#readme")
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
            putStringSet("repos", repositories.toStringSet())
        }
        Config.REPOS.apply {
            clear()
            addAll(repositories.toStringSet())
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