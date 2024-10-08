@file:Suppress("DEPRECATION")

package de.dertyp7214.rboardthememanager.screens

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.View.FOCUS_DOWN
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import de.dertyp7214.rboardcomponents.utils.doAsync
import de.dertyp7214.rboardthememanager.R
import de.dertyp7214.rboardthememanager.core.applyTheme
import de.dertyp7214.rboardthememanager.core.openDialog
import de.dertyp7214.rboardthememanager.core.su
import de.dertyp7214.rboardthememanager.utils.RootUtils

class ReadMoreReadFast : AppCompatActivity() {
    @SuppressLint("SetTextI18n")
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
        setContentView(R.layout.activity_read_more_read_fast)

        val button = findViewById<MaterialButton>(R.id.button)
        val scrollView = findViewById<ScrollView>(R.id.scrollView)
        val linearLayout = findViewById<LinearLayout>(R.id.linearLayout)

        var timeout = 5L

        button.setOnClickListener {
            timeout = 2L
            button.isEnabled = false
        }

        doAsync({
            val reader =
                ProcessBuilder().su("find . -print | sed -e \"s;[^/]*/;|____;g;s;____|; |;g\"").inputStream.bufferedReader()

            var line: String? = reader.readLine()
            while (line != null) {
                runOnUiThread {
                    linearLayout.addView(TextView(this).apply {
                        text = "Deleting: ${line?.replace(Regex("[|]*[|_]*"), "")?.trim()}"
                    })
                    if (linearLayout.childCount > 100) linearLayout.removeViewAt(0)
                    scrollView.fullScroll(FOCUS_DOWN)
                }
                Thread.sleep(timeout)
                line = reader.readLine()
            }
        }) {
            openDialog(R.string.reboot_now, R.string.reboot) {
                RootUtils.reboot()
            }
        }
    }
} 