@file:Suppress("DEPRECATION")

package de.dertyp7214.rboardthememanager.screens

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.topjohnwu.superuser.io.SuFile
import de.dertyp7214.rboardcomponents.utils.doAsync
import de.dertyp7214.rboardthememanager.R
import de.dertyp7214.rboardthememanager.adapter.ThemeAdapter
import de.dertyp7214.rboardthememanager.components.MarginItemDecoration
import de.dertyp7214.rboardthememanager.core.decodeBitmap
import de.dertyp7214.rboardthememanager.core.dp
import de.dertyp7214.rboardthememanager.core.install
import de.dertyp7214.rboardthememanager.core.isInstalled
import de.dertyp7214.rboardthememanager.core.openPreviewDialog
import de.dertyp7214.rboardthememanager.core.resize
import de.dertyp7214.rboardthememanager.core.screenWidth
import de.dertyp7214.rboardthememanager.data.ThemeDataClass
import de.dertyp7214.rboardthememanager.databinding.ActivityInstallPackBinding
import dev.chrisbanes.insetter.applyInsetter
import java.io.File
import java.util.regex.Pattern

class InstallPackActivity : AppCompatActivity() {

    private lateinit var binding: ActivityInstallPackBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.auto(Color.TRANSPARENT, Color.TRANSPARENT)
        )
        window.setDecorFitsSystemWindows(false)
        window.isNavigationBarContrastEnforced = false
        window.navigationBarColor = Color.TRANSPARENT
        super.onCreate(savedInstanceState)
        binding = ActivityInstallPackBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val fab = binding.fab
        val toolbar = binding.toolbar
        val recyclerview = binding.recyclerview

        toolbar.applyInsetter {
            type(statusBars = true) {
                margin()
            }
        }

        recyclerview.applyInsetter {
            type(navigationBars = true) {
                margin()
            }
        }

        toolbar.title = getString(R.string.install_themes, "0")

        val themes = arrayListOf<ThemeDataClass>()

        val adapter =
            ThemeAdapter(this, themes, ThemeAdapter.SelectionState.SELECTING, { _, adapter ->
                if (adapter.getSelected().isEmpty()) fab.isEnabled = false
                else if (!fab.isEnabled) fab.isEnabled = true
                toolbar.title =
                    getString(R.string.install_themes, adapter.getSelected().size.toString())
            }, ::openPreviewDialog) {}

        toolbar.navigationIcon = ContextCompat.getDrawable(
            this,
            R.drawable.ic_baseline_arrow_back_24
        )
        toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
        toolbar.setOnMenuItemClickListener {
            if (it.itemId == R.id.select_all) {
                if (adapter.getSelected().size == themes.size) adapter.clearSelection()
                else adapter.selectAll()
            }
            true
        }

        fab.setOnClickListener { _ ->
            val success = adapter.getSelected().map { it.install(recycle = true) }
            if (success.contains(false)) Toast.makeText(this, R.string.error, Toast.LENGTH_LONG)
                .also { toast = it }.show()
            else Toast.makeText(this, R.string.themes_installed, Toast.LENGTH_LONG)
                .also { toast = it }.show()
            setResult(RESULT_OK)
            finish()
        }

        recyclerview.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (dy > 0) fab.hide() else if (dy < 0) fab.show()
            }
        })

        doAsync({
            val maxWidth = screenWidth() - 68.dp(this)
            intent.getStringArrayListExtra("themes")?.let { paths ->
                val list = arrayListOf<ThemeDataClass>()
                paths.forEach { themePath ->
                    val file = File(themePath)
                    val name = file.nameWithoutExtension
                    val path = file.absolutePath
                    val imageFile = SuFile(file.absolutePath.removeSuffix(".zip"))
                    val image = imageFile.exists().let {
                        if (it) {
                            imageFile.decodeBitmap()?.let { bmp ->
                                val resized = bmp.resize(width = maxWidth)
                                if (resized != bmp) bmp.recycle()
                                resized
                            }
                        } else null
                    }
                    list.add(ThemeDataClass(image, name, path))
                }
                list
            } ?: listOf()
        }) {
            val meta = File(File(it.firstOrNull()?.path ?: "").parent, "pack.meta")
            themes.clear()
            themes.addAll(it)
            adapter.notifyDataChanged()
            themes.forEachIndexed { index, themeDataClass ->
                adapter[index] = !themeDataClass.isInstalled()
            }
            adapter.notifyDataChanged()
            toolbar.title =
                getString(R.string.install_themes, adapter.getSelected().size.toString())
            if (meta.exists()) {
                var name: String? = null
                var author: String? = null
                meta.readText().apply {
                    val matcher = Pattern.compile("(name|author)=(.*)\\n").matcher(this)
                    while (matcher.find()) {
                        when (matcher.group(1)) {
                            "name" -> matcher.group(2)?.let { metaName ->
                                name = metaName
                            }

                            "author" -> matcher.group(2)?.let { metaAuthor ->
                                author = metaAuthor
                            }
                        }
                    }
                }
                if (name != null || author != null) {
                    toolbar.subtitle = "$name by $author"
                }
            }
        }

        recyclerview.layoutManager = LinearLayoutManager(this)
        recyclerview.setHasFixedSize(true)
        recyclerview.adapter = adapter
        recyclerview.addItemDecoration(MarginItemDecoration(2.dp(this), all = true))
    }

    companion object {
        var toast: Toast? = null
    }
}