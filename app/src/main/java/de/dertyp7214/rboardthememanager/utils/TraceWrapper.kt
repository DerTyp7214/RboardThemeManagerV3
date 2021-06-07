package de.dertyp7214.rboardthememanager.utils

import android.os.Trace
import android.util.Log

class TraceWrapper(private val label: String, private val loggingEnabled: Boolean = false) {

    private var currentLabel = ""
    private var startTime = System.currentTimeMillis()

    fun end() {
        if (loggingEnabled) {
            val diff = System.currentTimeMillis() - startTime
            Log.d(label, "$currentLabel took: ${diff}ms")
        }
    }

    fun addSplit(label: String) {
        if (loggingEnabled) {
            val diff = System.currentTimeMillis() - startTime
            startTime = System.currentTimeMillis()
            if (currentLabel.isNotEmpty()) Log.d(this.label, "$currentLabel took: ${diff}ms")
            currentLabel = label
            Trace.endSection()
            Trace.beginSection(label)
        }
    }
}