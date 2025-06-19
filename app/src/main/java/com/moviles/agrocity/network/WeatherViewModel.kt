package com.moviles.agrocity.network

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class WeatherViewModel(
    private val repository: WeatherRepository = WeatherRepository()
) : ViewModel() {

    private val _weatherText = MutableStateFlow("Cargando clima...")
    val weatherText: StateFlow<String> = _weatherText

    fun fetchWeather(lat: String, lon: String) {
        viewModelScope.launch {
            val result = repository.getCurrentWeather(lat, lon)
            _weatherText.value = result.fold(
                onSuccess = {
                    "Clima actual: ${it.current.temp_c}°C"
                },
                onFailure = {
                    "No se pudo obtener el clima"
                }
            )
        }
    }
}
