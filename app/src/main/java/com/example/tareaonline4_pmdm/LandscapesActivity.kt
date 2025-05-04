package com.example.tareaonline4_pmdm

import android.content.Context
import android.content.SharedPreferences
import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class LandscapesActivity : AppCompatActivity() {

    private lateinit var animationText: TextView
    private lateinit var viewFlipper: ViewFlipper
    private lateinit var ratingBar: RatingBar
    private var mediaPlayer: MediaPlayer? = null
    private var initialX: Float = 0f
    private lateinit var preferences: SharedPreferences

    private val imageResIds = arrayOf(
        R.drawable.paisaje1,
        R.drawable.paisaje2,
        R.drawable.paisaje3,
        R.drawable.paisaje4
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_landscapes)

        animationText = findViewById(R.id.animationText)
        viewFlipper = findViewById(R.id.viewFlipper)
        ratingBar = findViewById(R.id.ratingBar)
        preferences = getSharedPreferences("ratings", Context.MODE_PRIVATE)

        startTextAnimations()
    }

    private fun startTextAnimations() {
        val zoomIn = AnimationUtils.loadAnimation(this, R.anim.zoom_in)
        val fadeOut = AnimationUtils.loadAnimation(this, R.anim.fade_out)

        animationText.startAnimation(zoomIn)
        playSound()

        zoomIn.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation?) {}

            override fun onAnimationEnd(animation: Animation?) {
                animationText.startAnimation(fadeOut)
            }

            override fun onAnimationRepeat(animation: Animation?) {}
        })

        fadeOut.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation?) {}

            override fun onAnimationEnd(animation: Animation?) {
                animationText.visibility = View.GONE
                setupViewFlipper()
            }

            override fun onAnimationRepeat(animation: Animation?) {}
        })
    }

    private fun setupViewFlipper() {
        viewFlipper.removeAllViews()

        for (imageRes in imageResIds) {
            val imageView = ImageView(this).apply {
                setImageResource(imageRes)
                scaleType = ImageView.ScaleType.FIT_CENTER
                adjustViewBounds = true
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
            }
            viewFlipper.addView(imageView)
        }

        viewFlipper.visibility = View.VISIBLE
        ratingBar.visibility = View.VISIBLE

        loadRatingForCurrentImage()

        ratingBar.setOnRatingBarChangeListener { _, rating, _ ->
            saveRatingForCurrentImage(rating)
        }

        viewFlipper.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    initialX = event.x
                    true
                }

                MotionEvent.ACTION_UP -> {
                    val finalX = event.x
                    if (initialX > finalX + 100) {
                        viewFlipper.showNext()
                        playSound()
                        loadRatingForCurrentImage()
                    } else if (initialX + 100 < finalX) {
                        viewFlipper.showPrevious()
                        playSound()
                        loadRatingForCurrentImage()
                    }
                    true
                }

                else -> false
            }
        }
    }

    private fun getCurrentImageKey(): String {
        return "rating_${viewFlipper.displayedChild}"
    }

    private fun saveRatingForCurrentImage(rating: Float) {
        val editor = preferences.edit()
        editor.putFloat(getCurrentImageKey(), rating)
        editor.apply()
    }

    private fun loadRatingForCurrentImage() {
        val rating = preferences.getFloat(getCurrentImageKey(), 0f)
        ratingBar.rating = rating
    }

    private fun playSound() {
        try {
            mediaPlayer?.release()
            mediaPlayer = MediaPlayer.create(this, R.raw.sonido).apply {
                setOnErrorListener { _, what, extra ->
                    Log.e("MediaPlayer", "Error code: $what, extra: $extra")
                    true
                }
                start()
            }
        } catch (e: Exception) {
            Log.e("Audio", "Error al reproducir sonido", e)
            try {
                val afd = assets.openFd("sonido.ogg")
                mediaPlayer = MediaPlayer().apply {
                    setDataSource(afd.fileDescriptor, afd.startOffset, afd.length)
                    prepare()
                    start()
                }
            } catch (ex: Exception) {
                Log.e("Audio", "Error al cargar desde assets", ex)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer?.release()
    }
}
