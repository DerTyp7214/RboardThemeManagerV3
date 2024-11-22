@file:Suppress("DEPRECATION")

package de.dertyp7214.rboardthememanager.screens

import android.annotation.SuppressLint
import android.content.Intent
import android.content.Intent.ACTION_SEND
import android.graphics.Color
import android.graphics.drawable.Icon
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import de.dertyp7214.rboardcomponents.components.SearchBar
import de.dertyp7214.rboardthememanager.R
import de.dertyp7214.rboardthememanager.adapter.ShareFlagsAdapter
import de.dertyp7214.rboardthememanager.components.LayoutManager
import de.dertyp7214.rboardthememanager.components.XMLFile
import de.dertyp7214.rboardthememanager.core.addCallback
import de.dertyp7214.rboardthememanager.core.applyTheme
import de.dertyp7214.rboardthememanager.core.getMapExtra
import de.dertyp7214.rboardthememanager.core.safeIcon
import de.dertyp7214.rboardthememanager.core.share
import de.dertyp7214.rboardthememanager.databinding.ActivityShareFlagsBinding
import de.dertyp7214.rboardthememanager.preferences.Flags
import dev.chrisbanes.insetter.applyInsetter
import java.io.File

class ShareFlags : AppCompatActivity() {

    private var flags: XMLFile = XMLFile()

    private lateinit var binding: ActivityShareFlagsBinding
    private lateinit var adapter: ShareFlagsAdapter
    private lateinit var searchBar: SearchBar
    private var import: Boolean = false
    private var isFlags: Boolean = true
    private var titleRes: Int = R.string.share_flags_title

    private val callbacks: ArrayList<OnBackPressedCallback> = arrayListOf()

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            applyTheme(shareFlags = true)
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
        binding = ActivityShareFlagsBinding.inflate(layoutInflater)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.run {
                WindowCompat.setDecorFitsSystemWindows(this, false)
            }
        }
        setContentView(binding.root)

        import = intent.getBooleanExtra("import", false)
        isFlags = intent.getBooleanExtra("isFlags", true)

        val toolbar = binding.toolbar
        searchBar = binding.searchBar
        val recyclerView = binding.recyclerview

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
        if (!isFlags) titleRes = R.string.share_prefs_title
        if (import) titleRes =
            if (isFlags) R.string.import_flags_title else R.string.import_prefs_title


        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        title = getString(titleRes, 0)

        flags = if (!import) if (isFlags) Flags.flagValues else Flags.prefValues
        else XMLFile(initMap = intent.getMapExtra("flags"))
        val orig = ArrayList(flags.simpleMap().map { it.key })
        val flagKeys = ArrayList(orig)

        adapter = ShareFlagsAdapter(flagKeys) {
            title = getString(titleRes, adapter.getSelectedFlags().size)
        }

        recyclerView.layoutManager = LayoutManager(this)
        recyclerView.adapter = adapter
        recyclerView.setHasFixedSize(true)

        searchBar.instantSearch = true
        searchBar.setOnSearchListener { filter ->
            flagKeys.clear()
            flagKeys.addAll(orig.filter { it.contains(filter, true) || filter.isBlank() })
            adapter.notifyDataSetChanged()
        }
        searchBar.setOnCloseListener {
            flagKeys.clear()
            flagKeys.addAll(orig)
            adapter.notifyDataSetChanged()
        }

        callbacks.forEach { it.remove() }
        onBackPressedDispatcher.addCallback(this, true, callbacks::add) {
            if (searchBar.focus) searchBar.clearText()
            else {
                isEnabled = false
                onBackPressedDispatcher.onBackPressed()
                isEnabled = true
            }
        }

        if (import) {
            adapter.selectAll()
            title = getString(titleRes, adapter.getSelectedFlags().size)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        if (import) menuInflater.inflate(R.menu.import_flags, menu)
        else menuInflater.inflate(R.menu.share_flags, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.share -> {
                val xml = XMLFile()
                adapter.getSelectedFlags().forEach {
                    flags.getValue(it)?.let { entry -> xml.setValue(entry) }
                }
                val file = File(filesDir, if (isFlags) "flags.rboard" else "prefs.rboard")
                if (adapter.getSelectedFlags().isNotEmpty()) {
                    file.writeText(xml.toString(if (isFlags) "FLAGS" else "PREFS"))
                    file.share(this, "text/xml", ACTION_SEND, R.string.share_flags)
                } else {
                    Toast.makeText(
                        this,
                        if (isFlags) R.string.select_flags else R.string.select_prefs,
                        Toast.LENGTH_SHORT
                    ).show()
                }
                true
            }

            R.id.select_all -> {
                val orig = ArrayList(flags.simpleMap().map { it.key })
                val flagKeys = ArrayList(orig)

                if (adapter.getSelectedFlags().size == flagKeys.size) {
                    adapter.clearSelection()
                } else {
                    adapter.selectAll()
                }

                title =
                    getString(titleRes, adapter.getSelectedFlags().size)
                true
            }

            R.id.apply_flags -> {
                Flags.setUpFlags()
                val selectedFlags = adapter.getSelectedFlags()
                selectedFlags.forEach {
                    Flags.setValue(
                        flags[it],
                        it,
                        if (isFlags) Flags.FILES.FLAGS else Flags.FILES.GBOARD_PREFERENCES
                    )
                }
                Flags.applyChanges()
                setResult(RESULT_OK, Intent().putExtra("size", selectedFlags.size))
                finish()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }


    override fun onDestroy() {
        callbacks.forEach { it.remove() }
        super.onDestroy()
        flags = XMLFile()
    }
}