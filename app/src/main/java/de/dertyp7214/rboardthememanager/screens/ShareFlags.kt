package de.dertyp7214.rboardthememanager.screens

import android.annotation.SuppressLint
import android.content.Intent
import android.content.Intent.ACTION_SEND
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import de.dertyp7214.rboardthememanager.R
import de.dertyp7214.rboardthememanager.adapter.ShareFlagsAdapter
import de.dertyp7214.rboardthememanager.components.SearchBar
import de.dertyp7214.rboardthememanager.core.getMapExtra
import de.dertyp7214.rboardthememanager.core.setXmlValue
import de.dertyp7214.rboardthememanager.core.share
import de.dertyp7214.rboardthememanager.databinding.ActivityShareFlagsBinding
import de.dertyp7214.rboardthememanager.preferences.Flags
import dev.chrisbanes.insetter.applyInsetter
import java.io.File

class ShareFlags : AppCompatActivity() {

    private var flags: Map<String, Any> = mapOf()

    private lateinit var binding: ActivityShareFlagsBinding
    private lateinit var adapter: ShareFlagsAdapter
    private lateinit var searchBar: SearchBar
    private var import: Boolean = false
    private var titleRes: Int = R.string.share_flags_title

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityShareFlagsBinding.inflate(layoutInflater)
        window.setDecorFitsSystemWindows(false)
        setContentView(binding.root)

        import = intent.getBooleanExtra("import", false)

        val toolbar = binding.toolbar
        searchBar = binding.searchBar
        val recyclerView = binding.recyclerview

        toolbar.applyInsetter {
            type(statusBars = true) {
                margin()
            }
        }

        if (import) titleRes = R.string.import_flags_title

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        title = getString(titleRes, 0)

        flags = if (!import) Flags.flagValues
        else intent.getMapExtra("flags")
        val orig = ArrayList(flags.map { it.key })
        val flagKeys = ArrayList(orig)

        adapter = ShareFlagsAdapter(flagKeys) {
            title = getString(titleRes, adapter.getSelectedFlags().size)
        }

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
        recyclerView.setHasFixedSize(true)

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

        if (import) {
            adapter.selectAll()
            title = getString(titleRes, adapter.getSelectedFlags().size)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        if (import) menuInflater.inflate(R.menu.import_flags, menu)
        else menuInflater.inflate(R.menu.share_flags, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.share -> {
                var xml = "<map/>"
                adapter.getSelectedFlags().forEach {
                    xml = xml.setXmlValue(flags[it], it)
                }
                val file = File(filesDir, "flags.rboard")
                file.writeText(xml)
                file.share(this, "text/xml", ACTION_SEND, R.string.share_flags)
                true
            }
            R.id.select_all -> {
                adapter.selectAll()
                title =
                    getString(titleRes, adapter.getSelectedFlags().size)
                true
            }
            R.id.apply_flags -> {
                Flags.setUpFlags()
                val selectedFlags = adapter.getSelectedFlags()
                selectedFlags.forEach {
                    Flags.setValue(flags[it], it, Flags.FILES.FLAGS)
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
        onBackPressed()
        return true
    }

    override fun onBackPressed() {
        if (searchBar.focus) searchBar.setText()
        else super.onBackPressed()
    }

    override fun onDestroy() {
        super.onDestroy()
        flags = mapOf()
    }
}