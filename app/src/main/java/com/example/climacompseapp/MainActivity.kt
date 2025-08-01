package com.example.climacompseapp

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.collectAsState
import androidx.core.content.ContextCompat
import com.example.climacompseapp.ui.MainScreen
import com.example.climacompseapp.ui.theme.ClimaCompseAppTheme
import com.example.climacompseapp.viewmodel.WeatherViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import androidx.activity.result.contract.ActivityResultContracts


class MainActivity : ComponentActivity() {

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val weatherViewModel = WeatherViewModel()

    private val requestMultiplePermissions = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        when {
            permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                    permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true -> {
                // Al menos uno de los permisos fue otorgado
                getLocationAndFetchWeather()
            }
            else -> {
                // Ningún permiso fue otorgado
                weatherViewModel.showError("Permiso de ubicación denegado. No se puede obtener la ubicación actual.")
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        setContent {
            ClimaCompseAppTheme {
                MainScreen(
                    onSearchClick = { city ->
                        weatherViewModel.fetchWeather(city)
                    },
                    onUseLocationClick = { handleLocationRequest() },
                    weatherInfo = weatherViewModel.weatherInfo.value,
                    forecast = weatherViewModel.forecast.collectAsState().value,
                    errorMessage = weatherViewModel.errorMessage.value
                )
            }
        }
    }

    private fun hasLocationPermission(): Boolean {
        val fine = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
        val coarse = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
        return fine == PackageManager.PERMISSION_GRANTED || coarse == PackageManager.PERMISSION_GRANTED
    }

    // Función que maneja la solicitud de ubicación
    private fun handleLocationRequest() {
        if (hasLocationPermission()) {
            getLocationAndFetchWeather()
        } else {
            requestLocationPermissions()
        }
    }

    private fun requestLocationPermissions() {
        requestMultiplePermissions.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )
    }

    @SuppressLint("MissingPermission")
    private fun getLocationAndFetchWeather() {
        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                weatherViewModel.fetchWeatherByLocation(location.latitude, location.longitude)
            } else {
                weatherViewModel.showError("No se pudo obtener la ubicación. Intenta activar el GPS.")
            }
        }.addOnFailureListener { exception ->
            weatherViewModel.showError("Error al obtener la ubicación: ${exception.message}")
        }
    }
}