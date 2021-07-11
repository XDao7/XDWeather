package com.xdao7.xdweather.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.xdao7.xdweather.R
import com.xdao7.xdweather.WeatherActivity
import com.xdao7.xdweather.databinding.FragmentCityBinding
import com.xdao7.xdweather.ui.adapter.CityAdapter
import com.xdao7.xdweather.utils.getStatusBarHeight

class CityFragment : Fragment() {

    private var _binding: FragmentCityBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: CityAdapter

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

        initData()
        initViews()
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun initData() {
        activity?.let {
            if (it is WeatherActivity) {
                it.viewModel.weatherLiveData.observe(viewLifecycleOwner) { result ->
                    val weather = result.getOrNull()
                    if (weather != null) {
                        it.viewModel.updateCityInfo(weather.cityInfo)
                        adapter.cityList = it.viewModel.city
                        adapter.notifyDataSetChanged()
                    }
                }
            }
        }
    }

    private fun initViews() {
        val colors = resources.getIntArray(R.array.colorCityList)
        val layoutManager = LinearLayoutManager(activity)
        binding.rvCity.layoutManager = layoutManager

        val activity = activity as WeatherActivity
        adapter = CityAdapter(this, colors, activity.viewModel.city)
        binding.rvCity.adapter = adapter
    }
}