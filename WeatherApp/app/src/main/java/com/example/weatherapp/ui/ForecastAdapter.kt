package com.example.weatherapp.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.weatherapp.R
import com.example.weatherapp.model.DailyForecast
import com.example.weatherapp.utils.PreferencesManager

/**
 * Адаптер для отображения 5-дневного прогноза
 */
class ForecastAdapter(
    private val preferencesManager: PreferencesManager
) : ListAdapter<DailyForecast, ForecastAdapter.ForecastViewHolder>(ForecastDiffCallback()) {
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ForecastViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_forecast, parent, false)
        return ForecastViewHolder(view)
    }
    
    override fun onBindViewHolder(holder: ForecastViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
    
    inner class ForecastViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvDayOfWeek: TextView = itemView.findViewById(R.id.tvDayOfWeek)
        private val tvDate: TextView = itemView.findViewById(R.id.tvDate)
        private val ivWeatherIcon: ImageView = itemView.findViewById(R.id.ivWeatherIcon)
        private val tvTempMax: TextView = itemView.findViewById(R.id.tvTempMax)
        private val tvTempMin: TextView = itemView.findViewById(R.id.tvTempMin)
        private val tvDescription: TextView = itemView.findViewById(R.id.tvDescription)
        private val tvHumidity: TextView = itemView.findViewById(R.id.tvHumidity)
        private val tvWind: TextView = itemView.findViewById(R.id.tvWind)
        
        fun bind(forecast: DailyForecast) {
            val unit = preferencesManager.getTemperatureUnit()
            val windUnit = preferencesManager.getWindSpeedUnit()
            val fontMultiplier = preferencesManager.getFontSizeMultiplier()
            
            tvDayOfWeek.text = forecast.dayOfWeek
            tvDate.text = forecast.date
            tvTempMax.text = "${forecast.tempMax.toInt()}$unit"
            tvTempMin.text = "${forecast.tempMin.toInt()}$unit"
            tvDescription.text = forecast.description
            tvHumidity.text = "${forecast.humidity}%"
            tvWind.text = "${forecast.windSpeed.toInt()} $windUnit"
            
            // Применяем размер шрифта
            applyFontSize(fontMultiplier)
            
            // Загружаем иконку погоды
            val iconUrl = "https://openweathermap.org/img/wn/${forecast.icon}@2x.png"
            Glide.with(itemView.context)
                .load(iconUrl)
                .placeholder(R.drawable.ic_weather_placeholder)
                .into(ivWeatherIcon)
        }
        
        private fun applyFontSize(multiplier: Float) {
            tvDayOfWeek.textSize = 16f * multiplier
            tvDate.textSize = 12f * multiplier
            tvTempMax.textSize = 18f * multiplier
            tvTempMin.textSize = 14f * multiplier
            tvDescription.textSize = 12f * multiplier
            tvHumidity.textSize = 12f * multiplier
            tvWind.textSize = 12f * multiplier
        }
    }
    
    class ForecastDiffCallback : DiffUtil.ItemCallback<DailyForecast>() {
        override fun areItemsTheSame(oldItem: DailyForecast, newItem: DailyForecast): Boolean {
            return oldItem.date == newItem.date
        }
        
        override fun areContentsTheSame(oldItem: DailyForecast, newItem: DailyForecast): Boolean {
            return oldItem == newItem
        }
    }
}
