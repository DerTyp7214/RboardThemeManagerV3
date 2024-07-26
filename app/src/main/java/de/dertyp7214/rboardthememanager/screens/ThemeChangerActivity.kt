package de.dertyp7214.rboardthememanager.screens

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.ContextThemeWrapper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.color.DynamicColors
import de.dertyp7214.rboardcomponents.core.applyThemeOverlay
import de.dertyp7214.rboardcomponents.utils.ThemeUtils
import de.dertyp7214.rboardcomponents.utils.ThemeUtils.getTheme
import de.dertyp7214.rboardthememanager.R
import de.dertyp7214.rboardthememanager.core.capitalize
import de.dertyp7214.rboardthememanager.core.get
import de.dertyp7214.rboardthememanager.core.getAttr
import de.dertyp7214.rboardthememanager.core.set
import de.dertyp7214.rboardthememanager.databinding.ActivityThemeChangerBinding
import dev.chrisbanes.insetter.applyInsetter

class ThemeChangerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityThemeChangerBinding
    private val currentTheme by lazy { ThemeUtils.getStyleName(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.auto(Color.TRANSPARENT, Color.TRANSPARENT)
        )

        window.setDecorFitsSystemWindows(false)

        val view: View = window.decorView
        window.isNavigationBarContrastEnforced = false
        window.navigationBarColor = Color.TRANSPARENT
        super.onCreate(savedInstanceState)
        binding = ActivityThemeChangerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val toolbar = binding.toolbar
        val applyButton = binding.applyButton
        val viewPager = binding.viewPager
        val dotsIndicator = binding.wormDotsIndicator

        val themes = ThemeUtils.APP_THEMES.toList()
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