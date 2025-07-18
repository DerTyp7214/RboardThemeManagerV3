@file:Suppress("DEPRECATION")

package de.dertyp7214.rboardthememanager.screens

import android.content.Context
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.ContextThemeWrapper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.annotation.StyleRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.color.DynamicColors
import de.dertyp7214.rboardcomponents.core.applyThemeOverlay
import de.dertyp7214.rboardcomponents.core.preferences
import de.dertyp7214.rboardcomponents.utils.THEMES
import de.dertyp7214.rboardcomponents.utils.ThemeUtils
import de.dertyp7214.rboardthememanager.R
import de.dertyp7214.rboardthememanager.core.applyTheme
import de.dertyp7214.rboardthememanager.core.capitalize
import de.dertyp7214.rboardthememanager.core.get
import de.dertyp7214.rboardthememanager.core.getAttr
import de.dertyp7214.rboardthememanager.core.set
import de.dertyp7214.rboardthememanager.databinding.ActivityThemeChangerBinding
import de.dertyp7214.rboardcomponents.*
import dev.chrisbanes.insetter.applyInsetter

class ThemeChangerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityThemeChangerBinding
    private val currentTheme by lazy { ThemeUtils.getStyleName(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            applyTheme()
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
        binding = ActivityThemeChangerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val toolbar = binding.toolbar
        val applyButton = binding.applyButton
        val viewPager = binding.viewPager
        val dotsIndicator = binding.wormDotsIndicator

        val themes = if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            APP_THEMES_Q.toList()
        } else if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) {
            APP_THEMES_S.toList()
        } else {
            ThemeUtils.APP_THEMES.toList()
        }

        toolbar.applyInsetter {
            type(statusBars = true) {
                margin()
            }
        }
        applyButton.applyInsetter {
            type(navigationBars = true) {
                margin()
            }
        }
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_toolbar_back_background)
        toolbar.navigationIcon = ContextCompat.getDrawable(
            this,R.drawable.ic_toolbar_back_background)

        title = getString(R.string.theme_changer)

        viewPager.adapter = ViewPagerAdapter(this, themes)
        viewPager.currentItem = themes.indexOfFirst { it.second == currentTheme }.let {
            if (it == -1) 0 else it
        }
        dotsIndicator.attachTo(viewPager)

        viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {}
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
            }

            override fun onPageSelected(page: Int) {
                applyButton.isEnabled = themes[page].second != currentTheme
            }
        })

        applyButton.isEnabled = themes[viewPager.currentItem].second != currentTheme

        applyButton.setOnClickListener {
            val index = viewPager.currentItem
            val theme = if (index == -1) themes[0] else themes[index]
            if (theme.second != currentTheme) {
                val themeRes = getTheme(this, theme.second)
                val context = wrapContext(this, themeRes)

                applyButton.isEnabled = false
                ThemeUtils.setStyle(context, theme.second)
                MainActivity.clearInstances()
                MainActivity::class.java[context]
                PreferencesActivity.clearInstances()
                PreferencesActivity::class.java[context] = {
                    putExtra("type", "settings")
                }
                ThemeChangerActivity::class.java[context]
                finish()
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }

    companion object {
        fun wrapContext(context: Context, theme: Int?): Context {
            return ContextThemeWrapper(
                context,
                theme ?: R.style.Theme_RboardThemeManagerV3
            ).let {
                if (theme == null) DynamicColors.wrapContextIfAvailable(it.apply {
                    applyThemeOverlay(
                        R.style.Theme_RboardThemeManagerV3
                    )
                }) else it
            }
        }

        fun getStyleName(context: Context): String {
            return if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
                context.preferences.getString("app_style", "default_style")
                    ?: "default_style"
            } else {
                context.preferences.getString("app_style", THEMES.DEFAULT.name)
                    ?: THEMES.DEFAULT.name
            }
        }

        val APP_THEMES_Q =
            mapOf(
                R.string.style_amoled to "amoled_style",
                R.string.style_apocyan to "apocyan_style",
                R.string.style_blue to "blue_style",
                R.string.style_brown_blue to "brown_blue_style",
                R.string.style_green to "green_style",
                R.string.style_green_brown to "green_brown_style",
                R.string.style_lavender_tonic to "lavender_tonic_style",
                R.string.style_lime to "lime_style",
                R.string.style_mary_blue to "mary_blue_style",
                R.string.style_monochrome to "monochrome_style",
                R.string.style_mud_pink to "mud_pink_style",
                R.string.style_night_rider to "night_rider_style",
                R.string.style_orange to "orange_style",
                R.string.style_pink to "pink_style",
                R.string.style_peach_pearl to "peach_pearl_style",
                R.string.style_rboard_v1 to "rboard_v1_style",
                R.string.style_rboard_v2 to "rboard_v2_style",
                R.string.style_red to "red_style",
                R.string.style_samoan_sun to "samoan_sun_style",
                R.string.style_teal to "teal_style",
                R.string.style_vert_pierre to "vert_pierre_style",
                R.string.style_yellow to "yellow_style",
                R.string.style_yellow_blue to "yellow_blue_style",
                R.string.style_default to "default_style"
            )
        val APP_THEMES_S = mapOf(
            R.string.style_amoled to THEMES.AMOLED.name,
            R.string.style_apocyan to THEMES.APOCYAN.name,
            R.string.style_blue to THEMES.BLUE.name,
            R.string.style_brown_blue to THEMES.BROWN_BLUE.name,
            R.string.style_green to THEMES.GREEN.name,
            R.string.style_green_brown to THEMES.GREEN_BROWN.name,
            R.string.style_lavender_tonic to THEMES.LAVENDER_TONIC.name,
            R.string.style_lime to THEMES.LIME.name,
            R.string.style_mary_blue to THEMES.MARY_BLUE.name,
            R.string.style_monochrome to THEMES.MONOCHROME.name,
            R.string.style_mud_pink to THEMES.MUD_PINK.name,
            R.string.style_night_rider to THEMES.NIGHT_RIDER.name,
            R.string.style_orange to THEMES.ORANGE.name,
            R.string.style_pink to THEMES.PINK.name,
            R.string.style_peach_pearl to THEMES.PEACH_PEARL.name,
            R.string.style_rboard_v1 to THEMES.RBOARD_V1.name,
            R.string.style_rboard_v2 to THEMES.RBOARD_V2.name,
            R.string.style_red to THEMES.RED.name,
            R.string.style_samoan_sun to THEMES.SAMOAN_SUN.name,
            R.string.style_teal to THEMES.TEAL.name,
            R.string.style_vert_pierre to THEMES.VERT_PIERRE.name,
            R.string.style_yellow to THEMES.YELLOW.name,
            R.string.style_yellow_blue to THEMES.YELLOW_BLUE.name,
            R.string.style_default to THEMES.DEFAULT.name
        )

        @StyleRes
        fun getTheme(context: Context, style: String = getStyleName(context)): Int? {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
                return when (style) {
                    "amoled_style" -> R.style.ThemeOverlay_RboardThemeManager_Colors_amoled
                    "apocyan_style" -> R.style.ThemeOverlay_RboardThemeManager_Colors_apocyan
                    "blue_style" -> R.style.ThemeOverlay_RboardThemeManager_Colors_blue
                    "brown_blue_style" -> R.style.ThemeOverlay_RboardThemeManager_Colors_brown_blue
                    "green_style" -> R.style.ThemeOverlay_RboardThemeManager_Colors_green
                    "green_brown_style" -> R.style.ThemeOverlay_RboardThemeManager_Colors_green_brown
                    "lavender_tonic_style" -> R.style.ThemeOverlay_RboardThemeManager_Colors_lavender_tonic
                    "lime_style" -> R.style.ThemeOverlay_RboardThemeManager_Colors_lime
                    "mary_blue_style" -> R.style.ThemeOverlay_RboardThemeManager_Colors_mary_blue
                    "monochrome_style" -> R.style.ThemeOverlay_RboardThemeManager_Colors_monochrome
                    "mud_pink_style" -> R.style.ThemeOverlay_RboardThemeManager_Colors_mud_pink
                    "night_rider_style" -> R.style.ThemeOverlay_RboardThemeManager_Colors_night_rider
                    "orange_style" -> R.style.ThemeOverlay_RboardThemeManager_Colors_orange
                    "pink_style" -> R.style.ThemeOverlay_RboardThemeManager_Colors_pink
                    "peach_pearl_style" -> R.style.ThemeOverlay_RboardThemeManager_Colors_peach_pearl
                    "rboard_v1_style" -> R.style.ThemeOverlay_RboardThemeManager_Colors_rboard_v1
                    "rboard_v2_style" -> R.style.ThemeOverlay_RboardThemeManager_Colors_rboard_v2
                    "red_style" -> R.style.ThemeOverlay_RboardThemeManager_Colors_red
                    "samoan_sun_style" -> R.style.ThemeOverlay_RboardThemeManager_Colors_samoan_sun
                    "teal_style" -> R.style.ThemeOverlay_RboardThemeManager_Colors_teal
                    "vert_pierre_style" -> R.style.ThemeOverlay_RboardThemeManager_Colors_vert_pierre
                    "yellow_style" -> R.style.ThemeOverlay_RboardThemeManager_Colors_yellow
                    "yellow_blue_style" -> R.style.ThemeOverlay_RboardThemeManager_Colors_yellow_blue
                    else -> null
                }
            } else if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) {
                return when (style) {
                    THEMES.AMOLED.name -> R.style.ThemeOverlay_RboardThemeManager_Colors_amoled
                    THEMES.APOCYAN.name -> R.style.ThemeOverlay_RboardThemeManager_Colors_apocyan
                    THEMES.BLUE.name -> R.style.ThemeOverlay_RboardThemeManager_Colors_blue
                    THEMES.BROWN_BLUE.name -> R.style.ThemeOverlay_RboardThemeManager_Colors_brown_blue
                    THEMES.GREEN.name -> R.style.ThemeOverlay_RboardThemeManager_Colors_green
                    THEMES.GREEN_BROWN.name -> R.style.ThemeOverlay_RboardThemeManager_Colors_green_brown
                    THEMES.LAVENDER_TONIC.name -> R.style.ThemeOverlay_RboardThemeManager_Colors_lavender_tonic
                    THEMES.LIME.name -> R.style.ThemeOverlay_RboardThemeManager_Colors_lime
                    THEMES.MARY_BLUE.name -> R.style.ThemeOverlay_RboardThemeManager_Colors_mary_blue
                    THEMES.MONOCHROME.name -> R.style.ThemeOverlay_RboardThemeManager_Colors_monochrome
                    THEMES.MUD_PINK.name -> R.style.ThemeOverlay_RboardThemeManager_Colors_mud_pink
                    THEMES.NIGHT_RIDER.name -> R.style.ThemeOverlay_RboardThemeManager_Colors_night_rider
                    THEMES.ORANGE.name -> R.style.ThemeOverlay_RboardThemeManager_Colors_orange
                    THEMES.PINK.name -> R.style.ThemeOverlay_RboardThemeManager_Colors_pink
                    THEMES.PEACH_PEARL.name -> R.style.ThemeOverlay_RboardThemeManager_Colors_peach_pearl
                    THEMES.RBOARD_V1.name -> R.style.ThemeOverlay_RboardThemeManager_Colors_rboard_v1
                    THEMES.RBOARD_V2.name -> R.style.ThemeOverlay_RboardThemeManager_Colors_rboard_v2
                    THEMES.RED.name -> R.style.ThemeOverlay_RboardThemeManager_Colors_red
                    THEMES.SAMOAN_SUN.name -> R.style.ThemeOverlay_RboardThemeManager_Colors_samoan_sun
                    THEMES.TEAL.name -> R.style.ThemeOverlay_RboardThemeManager_Colors_teal
                    THEMES.VERT_PIERRE.name -> R.style.ThemeOverlay_RboardThemeManager_Colors_vert_pierre
                    THEMES.YELLOW.name -> R.style.ThemeOverlay_RboardThemeManager_Colors_yellow
                    THEMES.YELLOW_BLUE.name -> R.style.ThemeOverlay_RboardThemeManager_Colors_yellow_blue
                    else -> null
                }
            } else {
                return when (style) {
                    THEMES.AMOLED.name -> R.style.ThemeOverlay_RboardThemeManager_Colors_amoled
                    THEMES.DYNAMIC_AMOLED.name -> R.style.ThemeOverlay_RboardThemeManager_Colors_dynamic_amoled
                    THEMES.APOCYAN.name -> R.style.ThemeOverlay_RboardThemeManager_Colors_apocyan
                    THEMES.BLUE.name -> R.style.ThemeOverlay_RboardThemeManager_Colors_blue
                    THEMES.BROWN_BLUE.name -> R.style.ThemeOverlay_RboardThemeManager_Colors_brown_blue
                    THEMES.GREEN.name -> R.style.ThemeOverlay_RboardThemeManager_Colors_green
                    THEMES.GREEN_BROWN.name -> R.style.ThemeOverlay_RboardThemeManager_Colors_green_brown
                    THEMES.LAVENDER_TONIC.name -> R.style.ThemeOverlay_RboardThemeManager_Colors_lavender_tonic
                    THEMES.LIME.name -> R.style.ThemeOverlay_RboardThemeManager_Colors_lime
                    THEMES.MARY_BLUE.name -> R.style.ThemeOverlay_RboardThemeManager_Colors_mary_blue
                    THEMES.MONOCHROME.name -> R.style.ThemeOverlay_RboardThemeManager_Colors_monochrome
                    THEMES.MUD_PINK.name -> R.style.ThemeOverlay_RboardThemeManager_Colors_mud_pink
                    THEMES.NIGHT_RIDER.name -> R.style.ThemeOverlay_RboardThemeManager_Colors_night_rider
                    THEMES.ORANGE.name -> R.style.ThemeOverlay_RboardThemeManager_Colors_orange
                    THEMES.PINK.name -> R.style.ThemeOverlay_RboardThemeManager_Colors_pink
                    THEMES.PEACH_PEARL.name -> R.style.ThemeOverlay_RboardThemeManager_Colors_peach_pearl
                    THEMES.RBOARD_V1.name -> R.style.ThemeOverlay_RboardThemeManager_Colors_rboard_v1
                    THEMES.RBOARD_V2.name -> R.style.ThemeOverlay_RboardThemeManager_Colors_rboard_v2
                    THEMES.RED.name -> R.style.ThemeOverlay_RboardThemeManager_Colors_red
                    THEMES.SAMOAN_SUN.name -> R.style.ThemeOverlay_RboardThemeManager_Colors_samoan_sun
                    THEMES.TEAL.name -> R.style.ThemeOverlay_RboardThemeManager_Colors_teal
                    THEMES.VERT_PIERRE.name -> R.style.ThemeOverlay_RboardThemeManager_Colors_vert_pierre
                    THEMES.YELLOW.name -> R.style.ThemeOverlay_RboardThemeManager_Colors_yellow
                    THEMES.YELLOW_BLUE.name -> R.style.ThemeOverlay_RboardThemeManager_Colors_yellow_blue
                    else -> null
                }
            }
        }

    }

    class ViewPagerAdapter(
        private val activity: ThemeChangerActivity,
        private val themes: List<Pair<Int, String>>
    ) : PagerAdapter() {
        override fun getCount(): Int = themes.size
        override fun isViewFromObject(view: View, `object`: Any): Boolean {
            return view == `object`
        }

        override fun instantiateItem(container: ViewGroup, position: Int): Any {
            val theme = getTheme(activity, themes[position].second)
            val ctx = wrapContext(activity, theme)
            val view = LayoutInflater.from(ctx)
                .inflate(R.layout.theme_preview, container, false)

            val toolbar = view.findViewById<MaterialToolbar>(R.id.toolbar)
            toolbar.title =
                themes[position].second.split("_").joinToString(" ") { it.capitalize(true) }
            toolbar.setTitleTextColor(ctx.getAttr(com.google.android.material.R.attr.colorOnPrimary))

            container.addView(view)

            return view
        }

        override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
            container.removeView(`object` as? View)
        }

    }
}