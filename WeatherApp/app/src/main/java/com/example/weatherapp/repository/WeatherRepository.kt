package com.example.weatherapp.repository

import com.example.weatherapp.api.RetrofitClient
import com.example.weatherapp.model.DailyForecast
import com.example.weatherapp.model.ForecastResponse
import com.example.weatherapp.model.WeatherResponse
import java.text.SimpleDateFormat
import java.util.*

/**
 * Репозиторий для получения данных о погоде
 */
class WeatherRepository {
    
    private val weatherService = RetrofitClient.weatherService
    
    /**
     * Получить текущую погоду
     */
    suspend fun getCurrentWeather(
        city: String,
        apiKey: String,
        units: String
    ): Result<WeatherResponse> {
        return try {
            val response = weatherService.getCurrentWeather(
                city = city,
                apiKey = apiKey,
                units = units
            )
            
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                val errorMessage = when (response.code()) {
                    404 -> "Город не найден"
                    401 -> "Неверный API ключ"
                    429 -> "Превышен лимит запросов"
                    else -> "Ошибка: ${response.code()}"
                }
                Result.failure(Exception(errorMessage))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Ошибка сети: проверьте подключение к интернету"))
        }
    }
    
    /**
     * Получить 5-дневный прогноз
     */
    suspend fun getForecast(
        city: String,
        apiKey: String,
        units: String
    ): Result<List<DailyForecast>> {
        return try {
            val response = weatherService.getForecast(
                city = city,
                apiKey = apiKey,
                units = units
            )
            
            if (response.isSuccessful && response.body() != null) {
                val dailyForecasts = processForecastData(response.body()!!)
                Result.success(dailyForecasts)
            } else {
                val errorMessage = when (response.code()) {
                    404 -> "Город не найден"
                    401 -> "Неверный API ключ"
                    429 -> "Превышен лимит запросов"
                    else -> "Ошибка: ${response.code()}"
                }
                Result.failure(Exception(errorMessage))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Ошибка сети: проверьте подключение к интернету"))
        }
    }
    
    /**
     * Преобразовать данные прогноза в дневной формат
     * API возвращает прогноз каждые 3 часа, группируем по дням
     */
    private fun processForecastData(forecast: ForecastResponse): List<DailyForecast> {
        val dailyMap = mutableMapOf<String, MutableList<com.example.weatherapp.model.ForecastItem>>()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        
        // Группируем по дате
        forecast.list.forEach { item ->
            val date = item.dtTxt.split(" ")[0]
            if (!dailyMap.containsKey(date)) {
                dailyMap[date] = mutableListOf()
            }
            dailyMap[date]?.add(item)
        }
        
        val dayOfWeekFormat = SimpleDateFormat("EEEE", Locale("ru"))
        val displayDateFormat = SimpleDateFormat("dd MMM", Locale("ru"))
        
        return dailyMap.entries
            .take(5) // Берём только 5 дней
            .map { (dateStr, items) ->
                val date = dateFormat.parse(dateStr) ?: Date()
                
                // Находим min/max температуру за день
                val tempMin = items.minOfOrNull { it.main.tempMin } ?: 0.0
                val tempMax = items.maxOfOrNull { it.main.tempMax } ?: 0.0
                
                // Берём данные из полуденного прогноза или первого доступного
                val midday = items.find { it.dtTxt.contains("12:00") } ?: items.first()
                
                DailyForecast(
                    date = displayDateFormat.format(date).replaceFirstChar { it.uppercase() },
                    dayOfWeek = dayOfWeekFormat.format(date).replaceFirstChar { it.uppercase() },
                    tempMin = tempMin,
                    tempMax = tempMax,
                    description = midday.weather.firstOrNull()?.description?.replaceFirstChar { it.uppercase() } ?: "",
                    icon = midday.weather.firstOrNull()?.icon ?: "01d",
                    humidity = midday.main.humidity,
                    windSpeed = midday.wind.speed
                )
            }
    }
}
