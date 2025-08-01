package com.example.climacompseapp.model

data class ForecastResponse(
    val list: List<ForecastItem>
)

data class ForecastItem(
    val dt_txt: String,
    val main: MainForecast,
    val weather: List<WeatherForecast>
)

data class MainForecast(
    val temp: Double
)

data class WeatherForecast(
    val description: String,
    val icon: String
)
