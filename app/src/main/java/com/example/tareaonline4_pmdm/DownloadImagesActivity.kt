package com.example.tareaonline4_pmdm

import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.squareup.picasso.Picasso
import kotlinx.coroutines.*
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.io.FileWriter
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class DownloadImagesActivity : AppCompatActivity() {

    private lateinit var viewFlipper: ViewFlipper
    private lateinit var progressBar: ProgressBar
    private lateinit var statusText: TextView
    private lateinit var btnStart: Button
    private val client = OkHttpClient()
    private var job: Job? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_download_images)

        viewFlipper = findViewById(R.id.viewFlipper)
        progressBar = findViewById(R.id.progressBar)
        statusText = findViewById(R.id.statusText)
        btnStart = findViewById(R.id.btnStart)

        btnStart.setOnClickListener {
            startDownloadAndAnimation()
        }
    }

    private fun startDownloadAndAnimation() {
        btnStart.isEnabled = false
        progressBar.visibility = View.VISIBLE
        statusText.text = "Descargando lista de imágenes..."

        job = CoroutineScope(Dispatchers.IO).launch {
            try {
                // Descargar la lista de imágenes
                val imageUrls = downloadImageList("https://raw.githubusercontent.com/JuanmaNaviaOrtega/imagenes-online/refs/heads/main/imagenes")

                // Descargar y mostrar imágenes
                withContext(Dispatchers.Main) {
                    statusText.text = "Descargando ${imageUrls.size} imágenes..."
                    progressBar.max = imageUrls.size
                }

                val downloadedImages = mutableListOf<String>()
                val errors = mutableListOf<String>()

                imageUrls.forEachIndexed { index, url ->
                    try {
                        // Verificar si la URL es válida
                        if (url.startsWith("http")) {
                            // Verificar si la imagen se puede cargar
                            runCatching {
                                Picasso.get().load(url).get()
                            }.onSuccess {
                                downloadedImages.add(url)
                                withContext(Dispatchers.Main) {
                                    progressBar.progress = index + 1
                                }
                            }.onFailure { e ->
                                logError(url, "Error al cargar imagen: ${e.message}", errors)
                            }
                        } else {
                            logError(url, "URL no válida", errors)
                        }
                    } catch (e: Exception) {
                        logError(url, "Error inesperado: ${e.message}", errors)
                    }
                }

                // Paso 3: Guardar errores en archivo
                if (errors.isNotEmpty()) {
                    saveErrorsToFile(errors)
                }

                // Paso 4: Mostrar animación
                withContext(Dispatchers.Main) {
                    if (downloadedImages.isNotEmpty()) {
                        statusText.text = "Mostrando ${downloadedImages.size} imágenes"
                        setupAnimation(downloadedImages)
                    } else {
                        statusText.text = "No se pudieron descargar imágenes. Ver errores."
                    }
                    progressBar.visibility = View.GONE
                    btnStart.isEnabled = true
                }

            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    statusText.text = "Error: ${e.message}"
                    progressBar.visibility = View.GONE
                    btnStart.isEnabled = true
                }
                logError("imagenes.txt", "Error al descargar lista: ${e.message}", mutableListOf())
            }
        }
    }

    private suspend fun downloadImageList(url: String): List<String> {
        return withContext(Dispatchers.IO) {
            val request = Request.Builder().url(url).build()
            val response = client.newCall(request).execute()
            if (!response.isSuccessful) throw IOException("Error al descargar lista: ${response.code}")

            response.body?.string()?.split("\n")?.filter { it.isNotBlank() } ?: emptyList()
        }
    }

    private fun logError(url: String, message: String, errorList: MutableList<String>) {
        val timestamp = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
        val error = "URL: $url | Error: $message | Fecha: $timestamp"
        errorList.add(error)
        Log.e("DownloadImages", error)
    }

    private fun saveErrorsToFile(errors: List<String>) {
        try {
            val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            val errorFile = File(downloadsDir, "errores.txt")

            FileWriter(errorFile, true).use { writer ->
                errors.forEach { error ->
                    writer.append(error).append("\n")
                }
            }
        } catch (e: Exception) {
            Log.e("DownloadImages", "Error al guardar archivo de errores", e)
        }
    }

    private fun setupAnimation(imageUrls: List<String>) {
        viewFlipper.removeAllViews()

        // Añadir todas las imágenes descargadas al ViewFlipper
        imageUrls.forEach { url ->
            val imageView = ImageView(this).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
                scaleType = ImageView.ScaleType.CENTER_CROP
            }

            Picasso.get()
                .load(url)
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.error_image)
                .into(imageView)

            viewFlipper.addView(imageView)
        }

        // Configurar animación automática
        viewFlipper.flipInterval = 3000
        viewFlipper.isAutoStart = true

        // Configurar animaciones de transición
        viewFlipper.setInAnimation(this, android.R.anim.fade_in)
        viewFlipper.setOutAnimation(this, android.R.anim.fade_out)

        // Mostrar ViewFlipperBotones para control manual
        setupManualControls()
    }

    private fun setupManualControls() {
        // Añadir botones de navegación (opcional)
        val btnPrevious = findViewById<Button>(R.id.btnPrevious)
        val btnNext = findViewById<Button>(R.id.btnNext)

        btnPrevious.setOnClickListener {
            viewFlipper.showPrevious()
            resetAnimationTimer()
        }

        btnNext.setOnClickListener {
            viewFlipper.showNext()
            resetAnimationTimer()
        }
    }

    private fun resetAnimationTimer() {
        // Reinicia el temporizador al navegar manualmente
        viewFlipper.stopFlipping()
        viewFlipper.startFlipping()
    }

    override fun onDestroy() {
        super.onDestroy()
        job?.cancel()
    }
}