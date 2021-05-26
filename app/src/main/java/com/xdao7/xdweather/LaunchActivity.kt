package com.xdao7.xdweather

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import com.xdao7.xdweather.databinding.ActivityLaunchBinding

class LaunchActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLaunchBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLaunchBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun onResume() {
        super.onResume()
        binding.groupText.visibility = View.VISIBLE

        Handler(Looper.getMainLooper()).postDelayed({
            WeatherActivity.actionStart(this@LaunchActivity)
            finish()
        }, 1500)
    }
}