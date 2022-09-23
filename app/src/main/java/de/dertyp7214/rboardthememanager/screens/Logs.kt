package de.dertyp7214.rboardthememanager.screens

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.dertyp7214.logs.fragments.Logs
import de.dertyp7214.rboardthememanager.R
import de.dertyp7214.rboardthememanager.core.applyTheme

class Logs : AppCompatActivity() {

    @SuppressLint("CommitTransaction")
    override fun onCreate(savedInstanceState: Bundle?) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            applyTheme(main = true)
        }
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_logs)

        supportFragmentManager.beginTransaction().apply {
            replace(R.id.frame, Logs())
            commit()
        }
    }
}