package com.example.weatherapp.model

import com.google.gson.annotations.SerializedName

/**
 * Модель ответа API для текущей погоды
 */
data class WeatherResponse(
    @SerializedName("name")
    val cityName: String,
    
    @SerializedName("main")
    val main: MainWeather,
    
    @SerializedName("weather")
    val weather: List<WeatherDescription>,
    
    @SerializedName("wind")
    val wind: Wind,
    
    @SerializedName("sys")
    val sys: Sys,
    
    @SerializedName("cod")
    val cod: Int
)

data class MainWeather(
    @SerializedName("temp")
    val temp: Double,
    
    @SerializedName("feels_like")
    val feelsLike: Double,
    
    @SerializedName("temp_min")
    val tempMin: Double,
    
    @SerializedName("temp_max")
    val tempMax: Double,
    
    @SerializedName("pressure")
    val pressure: Int,
    
    @SerializedName("humidity")
    val humidity: Int
)

data class WeatherDescription(
    @SerializedName("id")
    val id: Int,
    
    @SerializedName("main")
    val main: String,
    
    @SerializedName("description")
    val description: String,
    
    @SerializedName("icon")
    val icon: String
)

data class Wind(
    @SerializedName("speed")
    val speed: Double,
    
    @SerializedName("deg")
    val deg: Int
)

data class Sys(
    @SerializedName("country")
    val country: String,
    
    @SerializedName("sunrise")
    val sunrise: Long,
    
    @SerializedName("sunset")
    val sunset: Long
)
