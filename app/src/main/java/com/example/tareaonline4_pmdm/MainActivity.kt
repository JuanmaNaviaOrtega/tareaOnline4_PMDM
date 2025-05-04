package com.example.tareaonline4_pmdm

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        // Botón para el Ejercicio 1
        val btnLandscapes = findViewById<Button>(R.id.btnLandscapes)
        btnLandscapes.setOnClickListener {
            val intent = Intent(this, LandscapesActivity::class.java)
            startActivity(intent)
        }

        // Botón para el Ejercicio 2
        val btnDownloadImages = findViewById<Button>(R.id.btnDownloadImages)
        btnDownloadImages.setOnClickListener {
            startActivity(Intent(this, DownloadImagesActivity::class.java))
        }

    }
}