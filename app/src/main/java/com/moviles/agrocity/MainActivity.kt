package com.moviles.agrocity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Navegar directamente a LoginActivity
        startActivity(Intent(this, LoginActivity::class.java))

    }
}