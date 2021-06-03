package com.xdao7.xdweather.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.xdao7.xdweather.R
import com.xdao7.xdweather.databinding.ItemWarningBinding
import com.xdao7.xdweather.logic.model.response.qweather.WarningResponse

class WarningAdapter(
    private val context: Context,
    private val warningList: List<WarningResponse.Warning>
) : RecyclerView.Adapter<WarningAdapter.ViewHolder>() {

    inner class ViewHolder(binding: ItemWarningBinding) : RecyclerView.ViewHolder(binding.root) {
        val textTitle = binding.textTitle
        val textInfo = binding.textInfo
        val textTime = binding.textTime
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemWarningBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val warning = warningList[position]
        val index = warning.pubTime.lastIndexOf('+')
        holder.apply {
            textTitle.text = context.getString(R.string.str_waring, warning.typeName, warning.level)
            textInfo.text = warning.text
            textTime.text = context.getString(
                R.string.str_warning_time,
                warning.pubTime.substring(index - 5, index)
            )
        }
    }

    override fun getItemCount() = warningList.size
}