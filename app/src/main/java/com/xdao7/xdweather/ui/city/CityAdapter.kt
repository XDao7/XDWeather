package com.xdao7.xdweather.ui.city

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.xdao7.xdweather.SearchActivity
import com.xdao7.xdweather.R
import com.xdao7.xdweather.WeatherActivity
import com.xdao7.xdweather.databinding.ItemCityBinding
import com.xdao7.xdweather.logic.model.City

class CityAdapter(
    private val fragment: CityFragment,
    private val colors: IntArray,
    var cityList: City
) : RecyclerView.Adapter<CityAdapter.ViewHolder>() {

    companion object {
        private const val TYPE_CITY = 0
        private const val TYPE_ADD = 1
        private const val TYPE_DELETE = 2
        private const val TYPE_LOCATION = 3
    }

    inner class ViewHolder(binding: ItemCityBinding) : RecyclerView.ViewHolder(binding.root) {
        val root = binding.root
        val groupWeather = binding.groupWeather
        val textCity = binding.textCity
        val textTemp = binding.textTemp
        val textSky = binding.textSky
        val imgChoose = binding.imgChoose
        val imgTool = binding.imgTool
        val viewMask = binding.viewMask
        var type = TYPE_CITY
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemCityBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val activity = fragment.activity as WeatherActivity
        holder.apply {
            root.setCardBackgroundColor(colors[position])
            if (position > cityList.locations.size - 1 || cityList.position < 0) {
                groupWeather.visibility = View.GONE
                imgChoose.visibility = View.GONE
                imgTool.visibility = View.VISIBLE
                imgTool.setImageResource(R.drawable.ic_add)
                viewMask.visibility = View.GONE
                type = TYPE_ADD
            } else {
                val location = cityList.locations[position]
                groupWeather.visibility = View.VISIBLE
                imgChoose.visibility = View.VISIBLE
                imgTool.visibility = View.GONE
                viewMask.visibility = View.GONE
                textCity.text = location.name
                if (location.isCurrentLocation) {
                    type = TYPE_LOCATION
                    imgChoose.visibility = View.VISIBLE
                } else {
                    type = TYPE_CITY
                    imgChoose.visibility = View.GONE
                }

                if (cityList.info.size > 0) {
                    val info = cityList.info[position]
                    textTemp.text = fragment.getString(R.string.str_temp, info.temp)
                    textSky.text = info.text
                }
            }
            imgTool.setOnClickListener {
                when (holder.type) {
                    TYPE_ADD -> {
                        SearchActivity.actionStart(
                            activity,
                            WeatherActivity.SEARCH_PLACE_REQUEST_CODE
                        )
                    }
                    TYPE_DELETE -> {
                        activity.viewModel.deleteCity(position)
                        notifyItemRemoved(holder.adapterPosition)
                        notifyItemRangeChanged(position, itemCount - position)
                    }
                }
            }
            viewMask.setOnClickListener {
                holder.apply {
                    viewMask.visibility = View.GONE
                    imgTool.visibility = View.GONE
                    type = TYPE_CITY
                }
            }
            root.setOnClickListener {
                if (holder.type == TYPE_CITY || holder.type == TYPE_LOCATION) {
                    activity.apply {
                        viewModel.savePosition(position)
                        binding.dlHome.closeDrawers()
                        refreshWeather()
                    }
                }
            }
            root.setOnLongClickListener {
                if (holder.type == TYPE_CITY) {
                    holder.apply {
                        viewMask.visibility = View.VISIBLE
                        imgTool.visibility = View.VISIBLE
                        imgTool.setImageResource(R.drawable.ic_delete)
                        type = TYPE_DELETE
                    }
                    true
                } else {
                    false
                }
            }
        }
    }

    override fun getItemCount(): Int {
        var size = cityList.locations.size
        if (cityList.position < 0) {
            //无保存城市，并且定位失败
            size = 1
        } else {
            size += 1
            if (cityList.locations[0].isCurrentLocation) {
                //如果城市列表含有定位地点，则最多允许五项
                if (size > 5) {
                    size = 5
                }
            } else {
                //如果城市列表没有定位地点，则最多允许四项
                if (size > 4) {
                    size = 4
                }
            }
        }
        return size
    }
}