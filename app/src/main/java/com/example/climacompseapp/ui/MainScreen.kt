package com.example.climacompseapp.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.climacompseapp.model.ForecastResponse

@Composable
fun MainScreen(
    onSearchClick: (String) -> Unit,
    onUseLocationClick: () -> Unit,
    weatherInfo: String,
    forecast: ForecastResponse?,
    errorMessage: String
) {
    var city by remember { mutableStateOf(TextFieldValue("")) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Consulta del Clima", fontSize = 24.sp)
        Spacer(Modifier.height(16.dp))

        OutlinedTextField(
            value = city,
            onValueChange = { city = it },
            label = { Text("Ingresa una ciudad") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(16.dp))

        Button(
            onClick = { if (city.text.isNotBlank()) onSearchClick(city.text) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Buscar clima")
        }

        Spacer(Modifier.height(8.dp))

        Button(
            onClick = onUseLocationClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Usar mi ubicación")
        }

        Spacer(Modifier.height(24.dp))

        when {
            weatherInfo.isNotEmpty() -> {
                Text(weatherInfo, fontSize = 18.sp)
            }
            errorMessage.isNotEmpty() -> {
                Text(errorMessage, color = MaterialTheme.colorScheme.error)
            }
        }

        Spacer(Modifier.height(24.dp))

        forecast?.let {
            Text(
                text = "Pronóstico para los próximos 5 días:",
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(8.dp))

            Column {
                it.list.filterIndexed { index, _ -> index % 8 == 0 }.forEach { item ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(text = item.dt_txt.substring(0, 10), fontSize = 16.sp)
                            Text(text = "${item.main.temp}°C - ${item.weather[0].description}")
                        }
                    }
                }
            }
        }
    }
}

