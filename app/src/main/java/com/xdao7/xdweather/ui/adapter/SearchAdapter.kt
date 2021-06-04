package com.xdao7.xdweather.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.xdao7.xdweather.SearchActivity
import com.xdao7.xdweather.R
import com.xdao7.xdweather.databinding.ItemPlaceBinding
import com.xdao7.xdweather.logic.model.response.qweather.Location
import com.xdao7.xdweather.ui.fragment.SearchFragment

class SearchAdapter(private val fragment: SearchFragment, private val placeList: List<Location>) :
    RecyclerView.Adapter<SearchAdapter.ViewHolder>() {

    inner class ViewHolder(binding: ItemPlaceBinding) : RecyclerView.ViewHolder(binding.root) {
        val textPlaceName: TextView = binding.textPlaceName
        val textPlaceAddress: TextView = binding.textPlaceAddress
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemPlaceBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val place = placeList[position]
        holder.apply {
            textPlaceName.text = place.name
            textPlaceAddress.text =
                fragment.getString(R.string.str_place_adm, place.adm2, place.adm1, place.country)
            itemView.setOnClickListener {
                val activity = fragment.activity
                if (activity is SearchActivity) {
                    activity.addCity(place)
                }
            }
        }
    }

    override fun getItemCount(): Int = placeList.size
}