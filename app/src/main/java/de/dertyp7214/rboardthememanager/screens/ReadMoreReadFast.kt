package de.dertyp7214.rboardthememanager.screens

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View.FOCUS_DOWN
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import de.dertyp7214.rboardthememanager.R
import de.dertyp7214.rboardthememanager.core.openDialog
import de.dertyp7214.rboardthememanager.core.su
import de.dertyp7214.rboardthememanager.utils.RootUtils
import de.dertyp7214.rboardcomponents.utils.doAsync

class ReadMoreReadFast : AppCompatActivity() {
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
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