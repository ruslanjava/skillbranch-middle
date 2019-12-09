package ru.skillbranch.gameofthrones.app.ui.splash

import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import ru.skillbranch.gameofthrones.app.R
import ru.skillbranch.gameofthrones.app.databinding.ActivitySplashBinding

class SplashScreen : AppCompatActivity() {

    lateinit var splashLogo : ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)
        splashLogo = binding.ivSplashLogo
    }

    override fun onStart() {
        super.onStart()
        Glide.with(this)
            .load(R.drawable.spash)
            .centerCrop()
            .into(splashLogo)
    }

    override fun onDestroy() {
        Glide.with(this).clear(splashLogo)
        super.onDestroy()
    }

}