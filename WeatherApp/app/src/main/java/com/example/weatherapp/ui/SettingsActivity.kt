package com.example.weatherapp.ui

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.weatherapp.databinding.ActivitySettingsBinding
import com.example.weatherapp.utils.PreferencesManager

/**
 * Экран настроек приложения
 */
class SettingsActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivitySettingsBinding
    private lateinit var preferencesManager: PreferencesManager
    
    override fun onCreate(savedInstanceState: Bundle?) {
        preferencesManager = PreferencesManager(this)
        preferencesManager.applyTheme()
        
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Настройки"
        
        loadSettings()
        setupListeners()
    }
    
    private fun loadSettings() {
        binding.apply {
            // Единицы измерения
            switchUnits.isChecked = preferencesManager.isCelsius()
            updateUnitsLabel()
            
            // Тема
            when (preferencesManager.theme) {
                PreferencesManager.THEME_LIGHT -> rbLight.isChecked = true
                PreferencesManager.THEME_DARK -> rbDark.isChecked = true
                PreferencesManager.THEME_SYSTEM -> rbSystem.isChecked = true
            }
            
            // Размер шрифта
            when (preferencesManager.fontSize) {
                PreferencesManager.FONT_SMALL -> rbFontSmall.isChecked = true
                PreferencesManager.FONT_MEDIUM -> rbFontMedium.isChecked = true
                PreferencesManager.FONT_LARGE -> rbFontLarge.isChecked = true
            }
            
            // Город
            etCity.setText(preferencesManager.city)
            
            // API ключ
            etApiKey.setText(preferencesManager.apiKey)
        }
    }
    
    private fun setupListeners() {
        binding.apply {
            // Единицы измерения
            switchUnits.setOnCheckedChangeListener { _, isChecked ->
                preferencesManager.units = if (isChecked) {
                    PreferencesManager.UNITS_METRIC
                } else {
                    PreferencesManager.UNITS_IMPERIAL
                }
                updateUnitsLabel()
            }
            
            // Тема
            rgTheme.setOnCheckedChangeListener { _, checkedId ->
                val theme = when (checkedId) {
                    rbLight.id -> PreferencesManager.THEME_LIGHT
                    rbDark.id -> PreferencesManager.THEME_DARK
                    else -> PreferencesManager.THEME_SYSTEM
                }
                preferencesManager.theme = theme
            }
            
            // Размер шрифта
            rgFontSize.setOnCheckedChangeListener { _, checkedId ->
                val fontSize = when (checkedId) {
                    rbFontSmall.id -> PreferencesManager.FONT_SMALL
                    rbFontMedium.id -> PreferencesManager.FONT_MEDIUM
                    else -> PreferencesManager.FONT_LARGE
                }
                preferencesManager.fontSize = fontSize
                applyFontSize()
            }
            
            // Кнопка сохранения города
            btnSaveCity.setOnClickListener {
                val city = etCity.text.toString().trim()
                if (city.isNotEmpty()) {
                    preferencesManager.city = city
                    Toast.makeText(this@SettingsActivity, "Город сохранён", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this@SettingsActivity, "Введите название города", Toast.LENGTH_SHORT).show()
                }
            }
            
            // Кнопка сохранения API ключа
            btnSaveApiKey.setOnClickListener {
                val apiKey = etApiKey.text.toString().trim()
                if (apiKey.isNotEmpty()) {
                    preferencesManager.apiKey = apiKey
                    Toast.makeText(this@SettingsActivity, "API ключ сохранён", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this@SettingsActivity, "Введите API ключ", Toast.LENGTH_SHORT).show()
                }
            }
        }
        
        applyFontSize()
    }
    
    private fun updateUnitsLabel() {
        binding.tvUnitsLabel.text = if (binding.switchUnits.isChecked) {
            "Цельсий (°C)"
        } else {
            "Фаренгейт (°F)"
        }
    }
    
    private fun applyFontSize() {
        val multiplier = preferencesManager.getFontSizeMultiplier()
        
        binding.apply {
            tvUnitsTitle.textSize = 16f * multiplier
            tvUnitsLabel.textSize = 14f * multiplier
            tvThemeTitle.textSize = 16f * multiplier
            rbLight.textSize = 14f * multiplier
            rbDark.textSize = 14f * multiplier
            rbSystem.textSize = 14f * multiplier
            tvFontSizeTitle.textSize = 16f * multiplier
            rbFontSmall.textSize = 14f * multiplier
            rbFontMedium.textSize = 14f * multiplier
            rbFontLarge.textSize = 14f * multiplier
            tvCityTitle.textSize = 16f * multiplier
            tvApiKeyTitle.textSize = 16f * multiplier
        }
    }
    
    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }
}
