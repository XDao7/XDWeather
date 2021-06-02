package com.xdao7.xdweather.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.xdao7.xdweather.R
import com.xdao7.xdweather.databinding.ItemHourlyBinding
import com.xdao7.xdweather.logic.model.getSky
import com.xdao7.xdweather.logic.model.response.qweather.HourlyResponse

class HourlyAdapter(private val context: Context) :
    RecyclerView.Adapter<HourlyAdapter.ViewHolder>() {

    var hourly: List<HourlyResponse.Hourly> = ArrayList()

    inner class ViewHolder(binding: ItemHourlyBinding) : RecyclerView.ViewHolder(binding.root) {
        val textTime: TextView = binding.textTime
        val imageSky: ImageView = binding.imageSky
        val textTemp: TextView = binding.textTemp
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemHourlyBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val hour = hourly[position]
        val index = hour.fxTime.lastIndexOf('+')
        val sky = getSky(hour.icon)
        holder.apply {
            textTime.text = hour.fxTime.substring(index - 5, index)
            imageSky.setImageResource(sky.smallIcon)
            textTemp.text = context.getString(R.string.str_temp_hourly, hour.temp)
        }
    }

    override fun getItemCount() = hourly.size
}