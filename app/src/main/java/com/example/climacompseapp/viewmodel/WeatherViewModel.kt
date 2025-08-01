package com.example.climacompseapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.climacompseapp.data.WeatherRepository
import kotlinx.coroutines.launch
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.State
import com.example.climacompseapp.BuildConfig
import com.example.climacompseapp.data.RetrofitClient
import com.example.climacompseapp.model.WeatherResponse
import com.example.climacompseapp.model.ForecastResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class WeatherViewModel(
    private val repository: WeatherRepository = WeatherRepository()


) : ViewModel() {

    private val _forecast = MutableStateFlow<ForecastResponse?>(null)
    val forecast: StateFlow<ForecastResponse?> = _forecast
    private val _weatherInfo = mutableStateOf("")
    val weatherInfo: State<String> = _weatherInfo

    private val _errorMessage = mutableStateOf("")
    val errorMessage: State<String> = _errorMessage

    fun fetchWeather(city: String) {
        viewModelScope.launch {
            try {
                val weatherResult = RetrofitClient.weatherService.getWeatherByCity(city, BuildConfig.OPEN_WEATHER_API_KEY)
                val forecastResult = RetrofitClient.weatherService.getForecast(city, BuildConfig.OPEN_WEATHER_API_KEY)

                _weatherInfo.value = formatWeather(weatherResult)
                _forecast.value = forecastResult
                _errorMessage.value = ""
            } catch (e: Exception) {
                _weatherInfo.value = ""
                _forecast.value = null
                _errorMessage.value = "Ciudad no encontrada o sin conexión"
            }
        }
    }

    fun fetchWeatherByLocation(lat: Double, lon: Double) {
        viewModelScope.launch {
            try {
                val weatherResult = RetrofitClient.weatherService.getWeatherByLocation(lat, lon, BuildConfig.OPEN_WEATHER_API_KEY)
                val forecastResult = RetrofitClient.weatherService.getForecastByLocation(lat, lon, BuildConfig.OPEN_WEATHER_API_KEY)
                _weatherInfo.value = formatWeather(weatherResult)
                _forecast.value = forecastResult
                _errorMessage.value = ""
            } catch (e: Exception) {
                _weatherInfo.value = ""
                _forecast.value = null
                _errorMessage.value = "No se pudo obtener la ubicación o datos"
            }
        }
    }

    private fun formatWeather(weatherResult: WeatherResponse): String {
        val temp = weatherResult.main.temp
        val city = weatherResult.name
        val description = weatherResult.weather.firstOrNull()?.description ?: "Sin descripción"

        return "Ciudad: $city\nClima: $description\nTemperatura: $temp°C"
    }

    fun showError(message: String) {
        _errorMessage.value = message
    }
}
