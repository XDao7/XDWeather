package com.xdao7.xdweather

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.WindowInsetsController
import android.view.WindowManager
import com.xdao7.xdweather.databinding.ActivitySearchBinding
import com.xdao7.xdweather.logic.model.response.qweather.Location
import com.xdao7.xdweather.utils.startActivityForResult

class SearchActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySearchBinding

    companion object {
        fun actionStart(activity: Activity?, requestCode: Int) {
            activity?.let {
                startActivityForResult<SearchActivity>(it, requestCode) {}
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val decorView = window.decorView
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            decorView.windowInsetsController?.systemBarsBehavior = WindowInsetsController.BEHAVIOR_SHOW_BARS_BY_SWIPE
        } else {
            val option: Int = (View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_LAYOUT_STABLE)
            decorView.systemUiVisibility = option
        }
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = Color.TRANSPARENT
    }

    fun addCity(place: Location) {
        val intent = Intent().apply {
            putExtra("place", place)
        }
        setResult(RESULT_OK, intent)
        finish()
    }
}