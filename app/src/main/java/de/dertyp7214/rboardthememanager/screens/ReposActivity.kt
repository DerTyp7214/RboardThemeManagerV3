package de.dertyp7214.rboardthememanager.screens

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.edit
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import de.dertyp7214.rboardthememanager.Config
import de.dertyp7214.rboardthememanager.R
import de.dertyp7214.rboardthememanager.adapter.ReposAdapter
import de.dertyp7214.rboardthememanager.core.openDialog
import de.dertyp7214.rboardthememanager.core.openInputDialog
import de.dertyp7214.rboardthememanager.databinding.ActivityReposBinding

class ReposActivity : AppCompatActivity() {

    private lateinit var binding: ActivityReposBinding

    private val sharedPreferences by lazy { PreferenceManager.getDefaultSharedPreferences(this) }
    private val repositories = arrayListOf<String>()

    private var modified = false

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReposBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val toolbar = binding.reposToolbar
        val recyclerView = binding.repos

        val repos = sharedPreferences.getStringSet("repos", Config.REPOS.toSet())

        repos?.let(repositories::addAll)

        val adapter = ReposAdapter(repositories) {
            repositories.remove(it)
            recyclerView.adapter?.notifyDataSetChanged()
            modified = true
        }

        toolbar.title = getString(R.string.repos)

        toolbar.navigationIcon = ContextCompat.getDrawable(
            this,
            R.drawable.ic_baseline_arrow_back_24
        )
        toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
        toolbar.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.add -> {
                    openInputDialog(R.string.repository) { dialog, text ->
                        repositories.add(text)
                        adapter.notifyDataSetChanged()
                        modified = true
                        dialog.dismiss()
                    }
                }
                R.id.apply -> {
                    apply()
                    modified = false
                }
            }
            true
        }

        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.setHasFixedSize(true)
    }

    private fun apply() {
        sharedPreferences.edit {
            putStringSet("repos", repositories.toSet())
        }
        Config.REPOS.apply {
            clear()
            addAll(repositories)
        }
    }

    override fun onBackPressed() {
        if (modified) {
            openDialog(R.string.save_repos, R.string.apply, false, {
                super.onBackPressed()
            }) {
                apply()
                super.onBackPressed()
            }
        } else super.onBackPressed()
    }
}