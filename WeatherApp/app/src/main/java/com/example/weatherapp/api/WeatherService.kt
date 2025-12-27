package com.example.weatherapp.api

import com.example.weatherapp.model.ForecastResponse
import com.example.weatherapp.model.WeatherResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Retrofit интерфейс для OpenWeather API
 */
interface WeatherService {
    
    /**
     * Получить текущую погоду по названию города
     */
    @GET("weather")
    suspend fun getCurrentWeather(
        @Query("q") city: String,
        @Query("appid") apiKey: String,
        @Query("units") units: String = "metric",
        @Query("lang") lang: String = "ru"
    ): Response<WeatherResponse>
    
    /**
     * Получить 5-дневный прогноз (каждые 3 часа)
     */
    @GET("forecast")
    suspend fun getForecast(
        @Query("q") city: String,
        @Query("appid") apiKey: String,
        @Query("units") units: String = "metric",
        @Query("lang") lang: String = "ru"
    ): Response<ForecastResponse>
}
