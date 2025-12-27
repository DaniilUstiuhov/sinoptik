package com.example.weatherapp.model

import com.google.gson.annotations.SerializedName

/**
 * Модель ответа API для 5-дневного прогноза
 */
data class ForecastResponse(
    @SerializedName("cod")
    val cod: String,
    
    @SerializedName("message")
    val message: Int,
    
    @SerializedName("cnt")
    val count: Int,
    
    @SerializedName("list")
    val list: List<ForecastItem>,
    
    @SerializedName("city")
    val city: City
)

data class ForecastItem(
    @SerializedName("dt")
    val dt: Long,
    
    @SerializedName("main")
    val main: MainWeather,
    
    @SerializedName("weather")
    val weather: List<WeatherDescription>,
    
    @SerializedName("wind")
    val wind: Wind,
    
    @SerializedName("dt_txt")
    val dtTxt: String,
    
    @SerializedName("pop")
    val pop: Double // Вероятность осадков
)

data class City(
    @SerializedName("id")
    val id: Int,
    
    @SerializedName("name")
    val name: String,
    
    @SerializedName("country")
    val country: String
)

/**
 * Упрощённая модель для отображения в списке
 */
data class DailyForecast(
    val date: String,
    val dayOfWeek: String,
    val tempMin: Double,
    val tempMax: Double,
    val description: String,
    val icon: String,
    val humidity: Int,
    val windSpeed: Double
)
