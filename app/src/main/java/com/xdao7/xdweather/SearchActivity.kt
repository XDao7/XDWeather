package com.xdao7.xdweather

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.xdao7.xdweather.databinding.ActivitySearchBinding
import com.xdao7.xdweather.logic.model.response.qweather.Location
import com.xdao7.xdweather.utils.setFullScreenMode
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

        setFullScreenMode(window)
    }

    fun addCity(place: Location) {
        val intent = Intent().apply {
            putExtra("place", place)
        }
        setResult(RESULT_OK, intent)
        finish()
    }
}