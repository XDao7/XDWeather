package com.xdao7.xdweather

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.xdao7.xdweather.databinding.ActivityDailyBinding
import com.xdao7.xdweather.logic.model.response.qweather.DailyResponse
import com.xdao7.xdweather.ui.adapter.DailyAdapter
import com.xdao7.xdweather.ui.viewmodel.DailyViewModel
import com.xdao7.xdweather.utils.INTENT_DAILY
import com.xdao7.xdweather.utils.setFullScreenMode
import com.xdao7.xdweather.utils.startActivity

class DailyActivity : AppCompatActivity() {

    companion object {
        fun actionStart(context: Context, daily: ArrayList<DailyResponse.Daily>) {
            startActivity<DailyActivity>(context) {
                putExtra(INTENT_DAILY, daily)
            }
        }
    }

    private lateinit var binding: ActivityDailyBinding

    private val viewModel by lazy { ViewModelProvider(this).get(DailyViewModel::class.java) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDailyBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setFullScreenMode(window)

        intent?.getParcelableArrayListExtra<DailyResponse.Daily>(INTENT_DAILY)?.also {
            viewModel.dailyWeather = it
        }

        initViews()
    }

    private fun initViews() {
        binding.vpDaily.adapter = DailyAdapter(this, viewModel.dailyWeather)
    }
}