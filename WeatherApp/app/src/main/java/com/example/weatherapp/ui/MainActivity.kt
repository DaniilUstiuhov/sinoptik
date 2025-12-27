package com.example.weatherapp.ui

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.weatherapp.BuildConfig
import com.example.weatherapp.R
import com.example.weatherapp.databinding.ActivityMainBinding
import com.example.weatherapp.model.WeatherResponse
import com.example.weatherapp.repository.WeatherRepository
import com.example.weatherapp.utils.NetworkUtils
import com.example.weatherapp.utils.PreferencesManager
import kotlinx.coroutines.launch

/**
 * Главный экран приложения - отображение погоды
 */
class MainActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityMainBinding
    private lateinit var preferencesManager: PreferencesManager
    private lateinit var weatherRepository: WeatherRepository
    private lateinit var forecastAdapter: ForecastAdapter
    
    override fun onCreate(savedInstanceState: Bundle?) {
        preferencesManager = PreferencesManager(this)
        preferencesManager.applyTheme()
        
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        setSupportActionBar(binding.toolbar)
        
        weatherRepository = WeatherRepository()
        
        setupUI()
        setupRecyclerView()
        
        // Проверяем API ключ при первом запуске
        checkApiKey()
    }
    
    override fun onResume() {
        super.onResume()
        // Обновляем данные при возврате из настроек
        applyFontSize()
        loadWeatherData()
    }
    
    private fun setupUI() {
        // Кнопка обновления
        binding.btnRefresh.setOnClickListener {
            loadWeatherData()
        }
        
        // Swipe to refresh
        binding.swipeRefresh.setOnRefreshListener {
            loadWeatherData()
        }
        
        // Изменение города
        binding.btnChangeCity.setOnClickListener {
            showChangeCityDialog()
        }
    }
    
    private fun setupRecyclerView() {
        forecastAdapter = ForecastAdapter(preferencesManager)
        binding.rvForecast.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = forecastAdapter
            setHasFixedSize(true)
        }
    }
    
    private fun checkApiKey() {
        var apiKey = preferencesManager.apiKey
        
        // Если ключ не сохранён, пробуем из BuildConfig
        if (apiKey.isEmpty()) {
            apiKey = BuildConfig.OPENWEATHER_API_KEY
            if (apiKey != "YOUR_API_KEY_HERE" && apiKey.isNotEmpty()) {
                preferencesManager.apiKey = apiKey
            }
        }
        
        // Если всё ещё нет ключа, просим ввести
        if (apiKey.isEmpty() || apiKey == "YOUR_API_KEY_HERE") {
            showApiKeyDialog()
        } else {
            loadWeatherData()
        }
    }
    
    private fun showApiKeyDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_api_key, null)
        val editText = dialogView.findViewById<android.widget.EditText>(R.id.etApiKey)
        
        AlertDialog.Builder(this)
            .setTitle("API Ключ OpenWeather")
            .setMessage("Введите ваш API ключ с openweathermap.org")
            .setView(dialogView)
            .setCancelable(false)
            .setPositiveButton("Сохранить") { _, _ ->
                val key = editText.text.toString().trim()
                if (key.isNotEmpty()) {
                    preferencesManager.apiKey = key
                    loadWeatherData()
                } else {
                    Toast.makeText(this, "API ключ не может быть пустым", Toast.LENGTH_SHORT).show()
                    showApiKeyDialog()
                }
            }
            .show()
    }
    
    private fun showChangeCityDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_change_city, null)
        val editText = dialogView.findViewById<android.widget.EditText>(R.id.etCity)
        editText.setText(preferencesManager.city)
        
        AlertDialog.Builder(this)
            .setTitle("Изменить город")
            .setView(dialogView)
            .setPositiveButton("Сохранить") { _, _ ->
                val city = editText.text.toString().trim()
                if (city.isNotEmpty()) {
                    preferencesManager.city = city
                    loadWeatherData()
                }
            }
            .setNegativeButton("Отмена", null)
            .show()
    }
    
    private fun loadWeatherData() {
        // Проверяем сеть
        if (!NetworkUtils.isNetworkAvailable(this)) {
            showError("Нет подключения к интернету")
            binding.swipeRefresh.isRefreshing = false
            return
        }
        
        val apiKey = preferencesManager.apiKey
        if (apiKey.isEmpty() || apiKey == "YOUR_API_KEY_HERE") {
            showApiKeyDialog()
            return
        }
        
        showLoading(true)
        
        lifecycleScope.launch {
            // Загружаем текущую погоду
            val currentResult = weatherRepository.getCurrentWeather(
                city = preferencesManager.city,
                apiKey = apiKey,
                units = preferencesManager.units
            )
            
            currentResult.onSuccess { weather ->
                updateCurrentWeatherUI(weather)
            }.onFailure { error ->
                showError(error.message ?: "Неизвестная ошибка")
            }
            
            // Загружаем прогноз
            val forecastResult = weatherRepository.getForecast(
                city = preferencesManager.city,
                apiKey = apiKey,
                units = preferencesManager.units
            )
            
            forecastResult.onSuccess { forecasts ->
                forecastAdapter.submitList(forecasts)
                binding.tvForecastTitle.visibility = View.VISIBLE
            }.onFailure {
                binding.tvForecastTitle.visibility = View.GONE
            }
            
            showLoading(false)
        }
    }
    
    private fun updateCurrentWeatherUI(weather: WeatherResponse) {
        val unit = preferencesManager.getTemperatureUnit()
        val windUnit = preferencesManager.getWindSpeedUnit()
        
        binding.apply {
            tvCityName.text = "${weather.cityName}, ${weather.sys.country}"
            tvTemperature.text = "${weather.main.temp.toInt()}$unit"
            tvDescription.text = weather.weather.firstOrNull()?.description?.replaceFirstChar { it.uppercase() } ?: ""
            tvFeelsLike.text = "Ощущается как ${weather.main.feelsLike.toInt()}$unit"
            tvHumidity.text = "Влажность: ${weather.main.humidity}%"
            tvWind.text = "Ветер: ${weather.wind.speed.toInt()} $windUnit"
            tvPressure.text = "Давление: ${weather.main.pressure} гПа"
            
            // Иконка погоды
            val iconCode = weather.weather.firstOrNull()?.icon ?: "01d"
            val iconUrl = "https://openweathermap.org/img/wn/$iconCode@4x.png"
            Glide.with(this@MainActivity)
                .load(iconUrl)
                .placeholder(R.drawable.ic_weather_placeholder)
                .into(ivWeatherIcon)
            
            // Показываем контент
            contentGroup.visibility = View.VISIBLE
            errorGroup.visibility = View.GONE
        }
        
        applyFontSize()
    }
    
    private fun showLoading(isLoading: Boolean) {
        binding.apply {
            progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            swipeRefresh.isRefreshing = isLoading
        }
    }
    
    private fun showError(message: String) {
        binding.apply {
            contentGroup.visibility = View.GONE
            errorGroup.visibility = View.VISIBLE
            tvError.text = message
        }
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }
    
    private fun applyFontSize() {
        val multiplier = preferencesManager.getFontSizeMultiplier()
        
        binding.apply {
            tvCityName.textSize = 24f * multiplier
            tvTemperature.textSize = 64f * multiplier
            tvDescription.textSize = 18f * multiplier
            tvFeelsLike.textSize = 14f * multiplier
            tvHumidity.textSize = 14f * multiplier
            tvWind.textSize = 14f * multiplier
            tvPressure.textSize = 14f * multiplier
            tvForecastTitle.textSize = 18f * multiplier
        }
        
        // Обновляем адаптер для применения нового размера шрифта
        forecastAdapter.notifyDataSetChanged()
    }
    
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }
    
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings -> {
                startActivity(Intent(this, SettingsActivity::class.java))
                true
            }
            R.id.action_refresh -> {
                loadWeatherData()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
