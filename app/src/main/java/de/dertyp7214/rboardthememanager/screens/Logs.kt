package de.dertyp7214.rboardthememanager.screens

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.dertyp7214.logs.fragments.Logs
import de.dertyp7214.rboardthememanager.R

class Logs : AppCompatActivity() {

    @SuppressLint("CommitTransaction")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_logs)

        supportFragmentManager.beginTransaction().apply {
            replace(R.id.frame, Logs())
            commit()
        }
    }
}