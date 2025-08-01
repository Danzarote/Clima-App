package com.example.climacompseapp.data

import com.example.climacompseapp.BuildConfig
import com.example.climacompseapp.model.WeatherResponse

class WeatherRepository {

    suspend fun getWeather(city: String): WeatherResponse {
        return RetrofitClient.weatherService.getWeatherByCity(city, BuildConfig.OPEN_WEATHER_API_KEY)
    }
}
