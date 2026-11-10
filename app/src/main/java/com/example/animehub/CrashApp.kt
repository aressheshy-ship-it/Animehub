package com.example.animehub

import android.app.Activity
import android.app.Application
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ScrollView
import android.widget.TextView

class CrashApp : Application() {
    override fun onCreate() {
        super.onCreate()
        val proc = try { java.io.File("/proc/self/cmdline").readText() } catch (e: Exception) { "" }
        if (proc.contains(":crash")) return
        Thread.setDefaultUncaughtExceptionHandler { _, e ->
            try {
                val i = Intent(this, CrashActivity::class.java)
                    .putExtra("trace", Log.getStackTraceString(e))
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                startActivity(i)
                Thread.sleep(600)
            } catch (_: Throwable) {}
            android.os.Process.killProcess(android.os.Process.myPid())
            System.exit(10)
        }
    }
}

class CrashActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val t = TextView(this).apply {
            text = intent.getStringExtra("trace")?.take(3000)
            textSize = 10f
            setPadding(24, 64, 24, 24)
            setTextIsSelectable(true)
        }
        setContentView(ScrollView(this).apply { addView(t) })
    }
}
