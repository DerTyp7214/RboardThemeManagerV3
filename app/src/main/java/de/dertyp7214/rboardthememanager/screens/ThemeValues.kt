package de.dertyp7214.rboardthememanager.screens

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Configuration
import android.graphics.Shader
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.LayerDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.AttrRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import de.dertyp7214.colorutilsc.ColorUtilsC
import de.dertyp7214.rboardcomponents.utils.ThemeUtils.applyThemeOverlay
import de.dertyp7214.rboardthememanager.R
import de.dertyp7214.rboardthememanager.core.getAttr
import de.dertyp7214.rboardthememanager.core.openInputDialog
import de.dertyp7214.rboardthememanager.core.setClipboard
import de.dertyp7214.rboardthememanager.databinding.ActivityThemeValuesBinding
import com.google.android.material.R as MR

class ThemeValues : AppCompatActivity() {

    private lateinit var binding: ActivityThemeValuesBinding
    private val themeValues by lazy {
        listOf(
            fromColor(MR.attr.colorPrimary, "primary"),
            fromColor(MR.attr.colorOnPrimary, "onPrimary"),
            fromColor(MR.attr.colorPrimaryContainer, "primaryContainer"),
            fromColor(MR.attr.colorOnPrimaryContainer, "onPrimaryContainer"),

            fromColor(MR.attr.colorSecondary, "secondary"),
            fromColor(MR.attr.colorOnSecondary, "onSecondary"),
            fromColor(MR.attr.colorSecondaryContainer, "secondaryContainer"),
            fromColor(MR.attr.colorOnSecondaryContainer, "onSecondaryContainer"),

            fromColor(MR.attr.colorTertiary, "tertiary"),
            fromColor(MR.attr.colorOnTertiary, "onTertiary"),
            fromColor(MR.attr.colorTertiaryContainer, "tertiaryContainer"),
            fromColor(MR.attr.colorOnTertiaryContainer, "onTertiaryContainer"),

            fromColor(MR.attr.colorError, "error"),
            fromColor(MR.attr.colorOnError, "onError"),
            fromColor(MR.attr.colorErrorContainer, "errorContainer"),
            fromColor(MR.attr.colorOnErrorContainer, "onErrorContainer"),

            fromColor(MR.attr.colorSurface, "surface"),
            fromColor(MR.attr.colorOnSurface, "onSurface"),
            fromColor(MR.attr.colorSurfaceVariant, "surfaceVariant"),
            fromColor(MR.attr.colorOnSurfaceVariant, "onSurfaceVariant"),
            fromColor(MR.attr.colorSurfaceContainerHighest, "SurfaceContainerHighest"),
            fromColor(MR.attr.colorSurfaceContainerHigh, "surfaceContainerHigh"),
            fromColor(MR.attr.colorSurfaceContainer, "surfaceContainer"),
            fromColor(MR.attr.colorSurfaceContainerLow, "surfaceContainerLow"),
            fromColor(MR.attr.colorSurfaceContainerLowest, "surfaceContainerLowest"),
            fromColor(MR.attr.colorSurfaceInverse, "inverseSurface"),
            fromColor(MR.attr.colorOnSurfaceInverse, "inverseOnSurface"),

            fromColor(MR.attr.colorOutline, "outline"),
            fromColor(MR.attr.colorOutlineVariant, "outlineVariant"),

            fromColor(MR.attr.colorPrimaryFixed, "primaryFixed"),
            fromColor(MR.attr.colorOnPrimaryFixed, "onPrimaryFixed"),
            fromColor(MR.attr.colorPrimaryFixedDim, "primaryFixedDim"),
            fromColor(MR.attr.colorOnPrimaryFixedVariant, "onPrimaryFixedVariant"),
            fromColor(MR.attr.colorPrimaryInverse, "inversePrimary"),

            fromColor(MR.attr.colorSecondaryFixed, "secondaryFixed"),
            fromColor(MR.attr.colorOnSecondaryFixed, "onSecondaryFixed"),
            fromColor(MR.attr.colorSecondaryFixedDim, "secondaryFixedDim"),
            fromColor(MR.attr.colorOnSecondaryFixedVariant, "onSecondaryFixedVariant"),

            fromColor(MR.attr.colorTertiaryFixed, "tertiaryFixed"),
            fromColor(MR.attr.colorOnTertiaryFixed, "onTertiaryFixed"),
            fromColor(MR.attr.colorTertiaryFixedDim, "tertiaryFixedDim"),
            fromColor(MR.attr.colorOnTertiaryFixedVariant, "onTertiaryFixedVariant"),

            fromColor(MR.attr.colorSurface, "background"),
            fromColor(MR.attr.colorOnSurface, "onBackground"),
            fromColor(MR.attr.colorSurfaceBright, "surfaceBright"),
            fromColor(MR.attr.colorSurfaceDim, "surfaceDim"),
            fromColor(MR.attr.scrimBackground, "scrimBackground"),
        )
    }
    private val adapter by lazy { ThemeValueAdapter(this, themeValues) }

    @OptIn(ExperimentalStdlibApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityThemeValuesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val toolbar = binding.toolbar
        val recyclerView = binding.recyclerview
        val fab = binding.fab

        title = getString(R.string.theme_values)

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (dy > 0) fab.hide() else if (dy < 0) fab.show()
            }
        })

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.setHasFixedSize(true)
        recyclerView.adapter = adapter

        fab.setOnClickListener {
            openInputDialog(R.string.value_prefix) { dialog, value ->
                val lines = ArrayList<String>()

                listOf(
                    Configuration.UI_MODE_NIGHT_NO,
                    Configuration.UI_MODE_NIGHT_YES
                ).forEach { mode ->
                    val tempContext =
                        createConfigurationContext(Configuration(resources.configuration).apply {
                            uiMode = mode
                        }).let {
                            applyThemeOverlay(it, R.style.Theme_RboardThemeManagerV3)
                        }

                    val prefix =
                        if (tempContext.resources.getBoolean(R.bool.isDark)) "${value}_md_theme_dark_" else "${value}_md_theme_light_"
                    themeValues.forEach { themeValue ->
                        lines += "\t<color name=\"${prefix}${themeValue.name}\">#${
                            themeValue.attr.let {
                                if (it != null) tempContext.getAttr(it) else themeValue.value
                            }.toHexString(HexFormat.UpperCase).let {
                                if (it.startsWith("FF")) it.substring(2) else it
                            }
                        }</color>"
                    }
                }

                val colorsXml = """
                    |<?xml version="1.0" encoding="utf-8"?>
                    |<resources>
                    |    ${lines.joinToString("\n")}
                    |</resources>
                """.trimMargin()

                val themeXml = { dark: Boolean ->
                    """
                    |<?xml version="1.0" encoding="utf-8"?>
                    |<resources>
                    |    <style name="ThemeOverlay.RboardThemeManager.Colors_${value}" parent="">
                    |        <item name="colorPrimary">@color/${value}_md_theme_${if (dark) "dark" else "light"}_primary</item>
                    |        <item name="colorOnPrimary">@color/${value}_md_theme_${if (dark) "dark" else "light"}_onPrimary</item>
                    |        <item name="colorPrimaryContainer">@color/${value}_md_theme_${if (dark) "dark" else "light"}_primaryContainer</item>
                    |        <item name="colorOnPrimaryContainer">@color/${value}_md_theme_${if (dark) "dark" else "light"}_onPrimaryContainer</item>
                    |        <item name="colorSecondary">@color/${value}_md_theme_${if (dark) "dark" else "light"}_secondary</item>
                    |        <item name="colorOnSecondary">@color/${value}_md_theme_${if (dark) "dark" else "light"}_onSecondary</item>
                    |        <item name="colorSecondaryContainer">@color/${value}_md_theme_${if (dark) "dark" else "light"}_secondaryContainer</item>
                    |        <item name="colorOnSecondaryContainer">@color/${value}_md_theme_${if (dark) "dark" else "light"}_onSecondaryContainer</item>
                    |        <item name="colorTertiary">@color/${value}_md_theme_${if (dark) "dark" else "light"}_tertiary</item>
                    |        <item name="colorOnTertiary">@color/${value}_md_theme_${if (dark) "dark" else "light"}_onTertiary</item>
                    |        <item name="colorTertiaryContainer">@color/${value}_md_theme_${if (dark) "dark" else "light"}_tertiaryContainer</item>
                    |        <item name="colorOnTertiaryContainer">@color/${value}_md_theme_${if (dark) "dark" else "light"}_onTertiaryContainer</item>
                    |        <item name="colorError">@color/${value}_md_theme_${if (dark) "dark" else "light"}_error</item>
                    |        <item name="colorOnError">@color/${value}_md_theme_${if (dark) "dark" else "light"}_onError</item>
                    |        <item name="colorErrorContainer">@color/${value}_md_theme_${if (dark) "dark" else "light"}_errorContainer</item>
                    |        <item name="colorOnErrorContainer">@color/${value}_md_theme_${if (dark) "dark" else "light"}_onErrorContainer</item>
                    |        <item name="colorOutline">@color/${value}_md_theme_${if (dark) "dark" else "light"}_outline</item>
                    |        <item name="android:colorBackground">@color/${value}_md_theme_${if (dark) "dark" else "light"}_background</item>
                    |        <item name="colorOnBackground">@color/${value}_md_theme_${if (dark) "dark" else "light"}_onBackground</item>
                    |        <item name="colorSurface">@color/${value}_md_theme_${if (dark) "dark" else "light"}_surface</item>
                    |        <item name="colorOnSurface">@color/${value}_md_theme_${if (dark) "dark" else "light"}_onSurface</item>
                    |        <item name="colorSurfaceVariant">@color/${value}_md_theme_${if (dark) "dark" else "light"}_surfaceVariant</item>
                    |        <item name="colorOnSurfaceVariant">@color/${value}_md_theme_${if (dark) "dark" else "light"}_onSurfaceVariant</item>
                    |        <item name="colorSurfaceInverse">@color/${value}_md_theme_${if (dark) "dark" else "light"}_inverseSurface</item>
                    |        <item name="colorOnSurfaceInverse">@color/${value}_md_theme_${if (dark) "dark" else "light"}_inverseOnSurface</item>
                    |        <item name="colorPrimaryInverse">@color/${value}_md_theme_${if (dark) "dark" else "light"}_inversePrimary</item>
                    |        <item name="colorSurfaceContainerHighest">@color/${value}_md_theme_${if (dark) "dark" else "light"}_SurfaceContainerHighest</item>
                    |    </style>
                    |</resources>
                    """.trimMargin()
                }

                val xml = """
                    |// colors.xml
                    |$colorsXml

                    |// theme.xml
                    |${themeXml(false)}

                    |// theme.xml (dark)
                    |${themeXml(true)}
                """.trimMargin()

                setClipboard(xml)
                Toast.makeText(this, R.string.copied_to_clipboard, Toast.LENGTH_SHORT).show()

                dialog.dismiss()
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }

    private fun fromColor(@AttrRes color: Int, name: String? = null): ThemeValue {
        val identifierName = resources.getResourceEntryName(color)
        return ThemeValue(name ?: identifierName, getAttr(color), color)
    }

    private data class ThemeValue(val name: String, val value: Int, @AttrRes val attr: Int? = null)

    private class ThemeValueAdapter(
        private val context: Context,
        private val items: List<ThemeValue>
    ) :
        RecyclerView.Adapter<ThemeValueAdapter.ViewHolder>() {

        private val surfaceColor = context.getAttr(MR.attr.colorSurface)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            return ViewHolder(
                LayoutInflater.from(parent.context).inflate(R.layout.theme_element, parent, false)
            )
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.bind(items[position])
        }

        override fun getItemCount(): Int = items.size

        inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val root: ViewGroup by lazy { itemView.findViewById(R.id.root) }
            val hex: TextView by lazy { itemView.findViewById(R.id.hex) }

            @SuppressLint("SetTextI18n")
            @OptIn(ExperimentalStdlibApi::class)
            fun bind(value: ThemeValue) {
                val hexColor = "#${
                    value.value.toHexString(HexFormat.UpperCase).let {
                        if (it.startsWith("FF")) it.substring(2) else it
                    }
                }"

                if (ColorUtilsC.alpha(value.value) == 0xFF) root.setBackgroundColor(value.value)
                else {
                    val background = LayerDrawable(
                        arrayOf(
                            ContextCompat.getDrawable(context, R.drawable.trans)?.let {
                                val tile = BitmapDrawable(context.resources, it.toBitmap())
                                tile.setTileModeXY(
                                    Shader.TileMode.REPEAT,
                                    Shader.TileMode.REPEAT
                                )
                                tile
                            },
                            ColorDrawable(value.value)
                        )
                    )
                    root.background = background
                }

                hex.text = "${value.name} (${hexColor})"

                hex.setTextColor(
                    ColorUtilsC.invertColor(
                        ColorUtilsC.compositeColors(
                            value.value,
                            surfaceColor
                        )
                    )
                )

                root.setOnClickListener {
                    context.setClipboard(hexColor)
                    Toast.makeText(context, R.string.copied_to_clipboard, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}