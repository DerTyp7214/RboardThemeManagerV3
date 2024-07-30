package de.dertyp7214.rboardthememanager.screens

import android.graphics.Color
import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.dertyp7214.logs.fragments.Logs
import de.dertyp7214.rboardthememanager.R

class Logs : AppCompatActivity() {

    @SuppressLint("CommitTransaction")
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.auto(Color.TRANSPARENT, Color.TRANSPARENT)
        )

        window.setDecorFitsSystemWindows(false)

        val view: View = window.decorView
        window.isNavigationBarContrastEnforced = false
        window.navigationBarColor = Color.TRANSPARENT
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_logs)

        supportFragmentManager.beginTransaction().apply {
            replace(R.id.frame, Logs())
            commit()
        }
    }
}