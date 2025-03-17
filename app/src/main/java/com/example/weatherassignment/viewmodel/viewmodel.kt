package com.example.weatherassignment.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherassignment.model.WeatherData
import com.example.weatherassignment.model.WeatherApiService
import kotlinx.coroutines.launch

class WeatherViewModel : ViewModel() {

    private val _weatherData = MutableLiveData<WeatherData?>()
    val weatherData: LiveData<WeatherData?> get() = _weatherData

    private val _isLoading = MutableLiveData<Boolean>(false)
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> get() = _errorMessage

    private val weatherApiService = WeatherApiService.getService()

        fun fetchWeather(city: String, unitSystem: String = "metric") {
            _isLoading.value = true
            _errorMessage.value = null

            viewModelScope.launch {
                try {
                    val response = weatherApiService.fetchWeatherData(city, unitSystem = unitSystem)
                    _weatherData.postValue(response)
                    _errorMessage.value = null
                } catch (e: Exception) {
                    Log.e("WeatherViewModel", "Error fetching weather data", e)
                    _errorMessage.value = "Failed to load weather data. Please try again."
                } finally {
                    _isLoading.value = false
                }
            }
        }
    }