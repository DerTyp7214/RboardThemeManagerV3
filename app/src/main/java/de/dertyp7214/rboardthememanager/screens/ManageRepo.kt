@file:Suppress("unused", "SameParameterValue", "DEPRECATION")

package de.dertyp7214.rboardthememanager.screens

import android.annotation.SuppressLint
import android.content.ClipboardManager
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import de.dertyp7214.rboardcomponents.utils.doAsync
import de.dertyp7214.rboardthememanager.R
import de.dertyp7214.rboardthememanager.adapter.ManageRepoThemePackAdapter
import de.dertyp7214.rboardthememanager.components.LayoutManager
import de.dertyp7214.rboardthememanager.core.applyTheme
import de.dertyp7214.rboardthememanager.core.getTextFromUrl
import de.dertyp7214.rboardthememanager.core.openDialog
import de.dertyp7214.rboardthememanager.core.safeParse
import de.dertyp7214.rboardthememanager.core.showMaterial
import de.dertyp7214.rboardthememanager.data.ThemePack
import de.dertyp7214.rboardthememanager.databinding.ActivityManageRepoBinding
import de.dertyp7214.rboardthememanager.utils.TypeTokens
import dev.chrisbanes.insetter.applyInsetter
import org.json.JSONObject
import java.net.URL

class ManageRepo : AppCompatActivity() {

    private lateinit var binding: ActivityManageRepoBinding
    private lateinit var key: String

    private var enabled: Boolean = false

    private enum class Action(val key: String) {
        DELETE("delete"),
        SHARE("share"),
        ENABLE("enable"),
        DISABLE("disable")
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            applyTheme(installPack = true)
        }
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.Q) {
            enableEdgeToEdge(
                statusBarStyle = SystemBarStyle.auto(Color.TRANSPARENT, Color.TRANSPARENT)
            )
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.setDecorFitsSystemWindows(false)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            window.isNavigationBarContrastEnforced = false
            window.navigationBarColor = Color.TRANSPARENT
        }
        super.onCreate(savedInstanceState)
        binding = ActivityManageRepoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        key = intent.getStringExtra("key") ?: ""
        enabled = intent.getBooleanExtra("enabled", false)

        val toolbar = binding.toolbar
        val recyclerView = binding.recyclerView
        val copy = binding.copy

        toolbar.applyInsetter {
            type(statusBars = true) {
                margin()
            }
        }

        recyclerView.applyInsetter {
            type(navigationBars = true) {
                margin()
            }
        }
        val items = ArrayList<ThemePack>()

        val adapter = ManageRepoThemePackAdapter(items)

        recyclerView.layoutManager = LayoutManager(this)
        recyclerView.adapter = adapter
        recyclerView.setHasFixedSize(true)

        toolbar.title = getString(R.string.manage_repo)

        binding.address.text = key


        copy.setOnClickListener {
            val clipboard = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
            clipboard.setText(binding.address.getText())
            Snackbar.make(
                window.decorView,
                R.string.sounds_applied,
                Snackbar.LENGTH_LONG).showMaterial()
        }
        toolbar.menu.findItem(R.id.enabled).isChecked = enabled
        toolbar.navigationIcon =
            ContextCompat.getDrawable(this, R.drawable.ic_baseline_arrow_back_24)
        toolbar.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }
        toolbar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.delete ->
                    openDialog(
                        R.string.do_you_want_to_delete_repo,
                        R.string.delete_repo
                    ) { dialog ->
                        dialog.dismiss()
                        finishWithAction(Action.DELETE)
                    }
                R.id.enabled -> if (it.isCheckable) {
                    it.isChecked = !it.isChecked
                    setAction(if (it.isChecked) Action.ENABLE else Action.DISABLE)
                }
            }
            true
        }

        doAsync(URL(key)::getTextFromUrl) { text ->
            val themes: List<ThemePack> = try {
                Gson().fromJson(
                    text,
                    TypeTokens<List<ThemePack>>()
                )
            } catch (e: Exception) {
                e.printStackTrace()
                listOf()
            }
            binding.themePackCount.text = themes.size.toString()
            items.clear()
            items.addAll(themes)
            adapter.notifyDataSetChanged()
        }

        doAsync(URL(key.replace("list.json", "meta.json"))::getTextFromUrl) { text ->
            if (text.isNotEmpty()) {
                JSONObject().safeParse(text).getString("name").let { name ->
                    if (name.isNotEmpty()) toolbar.title = name
                }
            }
        }
    }

    private fun setAction(action: Action) {
        setResult(RESULT_OK, Intent().apply {
            putExtra("key", key)
            putExtra("action", action.key)
        })
    }

    private fun finishWithAction(action: Action) {
        setAction(action)
        finish()
    }
}