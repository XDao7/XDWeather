package com.xdao7.xdweather.ui.fragment

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.xdao7.xdweather.R
import com.xdao7.xdweather.WeatherActivity
import com.xdao7.xdweather.databinding.FragmentCityBinding
import com.xdao7.xdweather.ui.adapter.CityAdapter
import com.xdao7.xdweather.utils.ACTION_REFRESH_CITY
import com.xdao7.xdweather.utils.getStatusBarHeight

class CityFragment : Fragment() {

    private var _binding: FragmentCityBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: CityAdapter
    private lateinit var cityReceiver: CityReceiver

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCityBinding.inflate(inflater, container, false)

        binding.textTitle.setPadding(0, getStatusBarHeight(), 0, 0)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val filter = IntentFilter().apply {
            addAction(ACTION_REFRESH_CITY)
        }
        cityReceiver = CityReceiver()
        context?.let { LocalBroadcastManager.getInstance(it).registerReceiver(cityReceiver, filter) }
        initViews()
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onDestroy() {
        context?.let { LocalBroadcastManager.getInstance(it).unregisterReceiver(cityReceiver) }
        super.onDestroy()
    }

    private fun initViews() {
        val colors = resources.getIntArray(R.array.colorCityList)
        val layoutManager = LinearLayoutManager(activity)
        binding.rvCity.layoutManager = layoutManager

        val activity = activity as WeatherActivity
        adapter = CityAdapter(this, colors, activity.viewModel.city)
        binding.rvCity.adapter = adapter
    }

    fun update() {
        activity?.let {
            val activity = it as WeatherActivity
            adapter.cityList = activity.viewModel.city
            adapter.notifyDataSetChanged()
        }
    }

    inner class CityReceiver : BroadcastReceiver() {
        override fun onReceive(p0: Context?, p1: Intent?) {
            update()
        }
    }
}