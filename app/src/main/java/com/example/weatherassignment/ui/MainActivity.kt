package com.example.weatherassignment.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.weatherassignment.model.WeatherData
import com.example.weatherassignment.ui.theme.WeatherAssignmentTheme
import com.example.weatherassignment.viewmodel.WeatherViewModel
import androidx.compose.ui.Alignment
import com.example.weatherassignment.ui.theme.DarkBlue
import com.example.weatherassignment.ui.theme.DarkRed
import com.example.weatherassignment.ui.theme.MustardYellow

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WeatherAssignmentTheme {
                WeatherApp()
            }
        }
    }
}

@Composable
fun WeatherApp() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "info") {
        composable("main") { WeatherScreen(navController) }
        composable("info") { InfoScreen(navController) }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeatherScreen(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    viewModel: WeatherViewModel = viewModel()
) {
    var city by remember { mutableStateOf("") }
    var unitSystem by remember { mutableStateOf("metric") } // Default to Celsius
    val weatherData by viewModel.weatherData.observeAsState()
    val isLoading by viewModel.isLoading.observeAsState(false)
    val errorMessage by viewModel.errorMessage.observeAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Weather Forecast", color = DarkBlue) }
            )
        },
        content = { paddingValues ->
            Column(
                modifier = modifier
                    .padding(16.dp)
                    .padding(paddingValues)
                    .background(MaterialTheme.colorScheme.background),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                OutlinedTextField(
                    value = city,
                    onValueChange = { city = it },
                    label = { Text("Enter city name", color = DarkBlue) },
                    modifier = Modifier.fillMaxWidth().padding(8.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Unit system selector
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Unit System:", color = DarkBlue)
                    RadioButton(
                        selected = unitSystem == "metric",
                        onClick = { unitSystem = "metric" }
                    )
                    Text("Celsius", color = DarkBlue)
                    RadioButton(
                        selected = unitSystem == "imperial",
                        onClick = { unitSystem = "imperial" }
                    )
                    Text("Fahrenheit", color = DarkBlue)
                }

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = {
                        if (city.isNotBlank()) {
                            viewModel.fetchWeather(city, unitSystem)
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isLoading,
                    colors = ButtonDefaults.buttonColors(containerColor = MustardYellow)
                ) {
                    Text("Get Weather", color = Color.White)
                }

                Spacer(modifier = Modifier.height(16.dp))

                weatherData?.let { data ->
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Weather in: $city",
                            style = MaterialTheme.typography.headlineSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = DarkBlue
                            )
                        )
                        Text(
                            text = "Temperature: ${data.main.temp}°${if (unitSystem == "metric") "C" else "F"}",
                            style = MaterialTheme.typography.bodyMedium.copy(color = DarkBlue)
                        )
                        Text(
                            text = "Humidity: ${data.main.humidity}%",
                            style = MaterialTheme.typography.bodyMedium.copy(color = MustardYellow)
                        )
                        Text(
                            text = "Environment: ${data.weather[0].description}",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontStyle = FontStyle.Italic,
                                color = DarkBlue
                            )
                        )
                    }
                }

                if (isLoading) {
                    CircularProgressIndicator(color = MustardYellow)
                }

                errorMessage?.let {
                    Text(text = it, color = DarkRed)
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = { navController.navigate("info") },
                    colors = ButtonDefaults.buttonColors(containerColor = DarkBlue)
                ) {
                    Text("Back to Home", color = Color.White)
                }
            }
        }
    )
}

@Composable
fun InfoScreen(navController: NavHostController) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            "Get Your Weather Forecast",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            "Find information about the weather from any city. Data taken from OpenWeather API.",
            style = MaterialTheme.typography.bodyMedium
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { navController.navigate("main") },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("City Selection", color = MaterialTheme.colorScheme.onPrimary)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun WeatherAppPreview() {
    WeatherAssignmentTheme {
        WeatherApp()
    }
}