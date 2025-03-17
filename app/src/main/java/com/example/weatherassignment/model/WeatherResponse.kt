package com.example.weatherassignment.model

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

// Constants for API configuration
private const val API_BASE_URL = "https://api.openweathermap.org/data/2.5/"
private const val WEATHER_API_KEY = "87097291fc5944447789d2625cc4fa76"

// Data classes for API response
data class WeatherData(
    val main: TemperatureDetails,
    val weather: List<WeatherCondition>
)

data class TemperatureDetails(
    val temp: Double,
    val humidity: Int
)

data class WeatherCondition(
    val description: String
)

interface WeatherApiService {

    @GET("weather")
    suspend fun fetchWeatherData(
        @Query("q") cityName: String,
        @Query("appid") apiKey: String = WEATHER_API_KEY,
        @Query("units") unitSystem: String = "metric" //default as degrees c
    ): WeatherData

    companion object {

        private var apiServiceInstance: WeatherApiService? = null

        fun getService(): WeatherApiService {
            if (apiServiceInstance == null) {
                apiServiceInstance = createRetrofitInstance().create(WeatherApiService::class.java)
            }
            return apiServiceInstance!!
        }

        private fun createRetrofitInstance(): Retrofit {
            return Retrofit.Builder()
                .baseUrl(API_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }
    }
}