package com.example.weatherapp.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.weatherapp.data.db.ForecastEntity
import com.example.weatherapp.databinding.ItemForecastBinding
import com.example.weatherapp.util.Constants
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

class ForecastAdapter : ListAdapter<ForecastEntity, ForecastAdapter.ForecastViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ForecastViewHolder {
        val binding = ItemForecastBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ForecastViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ForecastViewHolder, position: Int) {
        val currentItem = getItem(position)
        holder.bind(currentItem)
    }

    class ForecastViewHolder(private val binding: ItemForecastBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: ForecastEntity) {
            binding.apply {
                tvForecastTemp.text = "%.0f°C".format(item.temperature)

                val instant = Instant.ofEpochSecond(item.dt)
                val dateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault())
                val formatter = DateTimeFormatter.ofPattern("EEE, HH:mm", Locale("tr"))
                tvForecastDate.text = dateTime.format(formatter)

                val iconUrl = "${Constants.BASE_IMAGE_URL}${item.iconCode}@2x.png"
                Glide.with(itemView.context).load(iconUrl).into(ivForecastIcon)
            }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<ForecastEntity>() {
        override fun areItemsTheSame(oldItem: ForecastEntity, newItem: ForecastEntity) =
            oldItem.dt == newItem.dt

        override fun areContentsTheSame(oldItem: ForecastEntity, newItem: ForecastEntity) =
            oldItem == newItem
    }
}