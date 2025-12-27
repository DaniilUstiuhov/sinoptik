package com.example.weatherapp.utils

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate

/**
 * Менеджер настроек приложения (SharedPreferences)
 */
class PreferencesManager(context: Context) {
    
    private val prefs: SharedPreferences = context.getSharedPreferences(
        PREFS_NAME,
        Context.MODE_PRIVATE
    )
    
    companion object {
        private const val PREFS_NAME = "weather_app_prefs"
        
        // Ключи
        private const val KEY_CITY = "city"
        private const val KEY_UNITS = "units"
        private const val KEY_THEME = "theme"
        private const val KEY_FONT_SIZE = "font_size"
        private const val KEY_API_KEY = "api_key"
        
        // Значения по умолчанию
        const val DEFAULT_CITY = "Moscow"
        const val UNITS_METRIC = "metric"      // Цельсий
        const val UNITS_IMPERIAL = "imperial"  // Фаренгейт
        
        const val THEME_LIGHT = 0
        const val THEME_DARK = 1
        const val THEME_SYSTEM = 2
        
        const val FONT_SMALL = 0
        const val FONT_MEDIUM = 1
        const val FONT_LARGE = 2
    }
    
    // Город
    var city: String
        get() = prefs.getString(KEY_CITY, DEFAULT_CITY) ?: DEFAULT_CITY
        set(value) = prefs.edit().putString(KEY_CITY, value).apply()
    
    // Единицы измерения (metric/imperial)
    var units: String
        get() = prefs.getString(KEY_UNITS, UNITS_METRIC) ?: UNITS_METRIC
        set(value) = prefs.edit().putString(KEY_UNITS, value).apply()
    
    // Тема (0 - светлая, 1 - тёмная, 2 - системная)
    var theme: Int
        get() = prefs.getInt(KEY_THEME, THEME_SYSTEM)
        set(value) {
            prefs.edit().putInt(KEY_THEME, value).apply()
            applyTheme(value)
        }
    
    // Размер шрифта (0 - маленький, 1 - средний, 2 - большой)
    var fontSize: Int
        get() = prefs.getInt(KEY_FONT_SIZE, FONT_MEDIUM)
        set(value) = prefs.edit().putInt(KEY_FONT_SIZE, value).apply()
    
    // API ключ
    var apiKey: String
        get() = prefs.getString(KEY_API_KEY, "") ?: ""
        set(value) = prefs.edit().putString(KEY_API_KEY, value).apply()
    
    /**
     * Проверить, использовать ли Цельсий
     */
    fun isCelsius(): Boolean = units == UNITS_METRIC
    
    /**
     * Получить символ единицы температуры
     */
    fun getTemperatureUnit(): String = if (isCelsius()) "°C" else "°F"
    
    /**
     * Получить единицу скорости ветра
     */
    fun getWindSpeedUnit(): String = if (isCelsius()) "м/с" else "миль/ч"
    
    /**
     * Применить тему
     */
    fun applyTheme(themeMode: Int = theme) {
        when (themeMode) {
            THEME_LIGHT -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            THEME_DARK -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            THEME_SYSTEM -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        }
    }
    
    /**
     * Получить множитель размера шрифта
     */
    fun getFontSizeMultiplier(): Float {
        return when (fontSize) {
            FONT_SMALL -> 0.85f
            FONT_MEDIUM -> 1.0f
            FONT_LARGE -> 1.2f
            else -> 1.0f
        }
    }
}
