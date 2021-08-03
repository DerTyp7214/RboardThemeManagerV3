package de.dertyp7214.rboardthememanager.utils

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
import de.Maxr1998.modernpreferences.PreferenceScreen
import de.Maxr1998.modernpreferences.helpers.onClick
import de.Maxr1998.modernpreferences.helpers.switch
import de.dertyp7214.rboardthememanager.Config
import de.dertyp7214.rboardthememanager.R
import de.dertyp7214.rboardthememanager.core.openDialog
import de.dertyp7214.rboardthememanager.core.openInputDialog
import de.dertyp7214.rboardthememanager.core.toMap
import de.dertyp7214.rboardthememanager.core.toSet
import de.dertyp7214.rboardthememanager.preferences.AbstractMenuPreference

class Repos(private val activity: AppCompatActivity, private val onRequestReload: () -> Unit) :
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
                                repositories.remove(key)
                                onRequestReload()
                            }
                            "disable" -> {
                                repositories[key] = false
                                onRequestReload()
                            }
                            "enable" -> {
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
        repositories.forEach { (key, value) ->
            builder.switch(key) {
                title = key.replace("https://raw.githubusercontent.com/", "...")
                defaultValue = value
                onClick {
                    TODO("Open Reposcreen with info like themes included and option to disable, remove or share")
                    false
                }
            }
        }
    }

    override fun loadMenu(menuInflater: MenuInflater, menu: Menu?) {
        if (menu != null) menuInflater.inflate(R.menu.repos, menu)
    }

    override fun onMenuClick(menuItem: MenuItem): Boolean {
        when (menuItem.itemId) {
            R.id.add -> {
                activity.openInputDialog(R.string.repository) { dialog, text ->
                    repositories[text] = true
                    onRequestReload()
                    modified = true
                    dialog.dismiss()
                }
            }
            R.id.apply -> {
                apply()
                modified = false
            }
        }
        return true
    }

    override fun getExtraView(): View? = null

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