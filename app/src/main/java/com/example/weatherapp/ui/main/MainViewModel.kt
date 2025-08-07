package com.example.weatherapp.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.data.model.WeatherResponse
import com.example.weatherapp.data.repository.WeatherRepository
import com.example.weatherapp.util.Constants
import kotlinx.coroutines.launch
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(private val weatherRepository: WeatherRepository) : ViewModel() {

    // LiveData to hold to weather data. UI will observe this.
    private val _weatherData = MutableLiveData<WeatherResponse>()
    val weatherData: LiveData<WeatherResponse> = _weatherData

    // LiveData for error massages.
    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    // LiveData for loading state.
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    fun fetchWeather(cityName: String) {
        _isLoading.value = true
        // Launch a coroutline in the viewModelScope.
        // This scope is tied to the ViewModel's lifecycle.
        viewModelScope.launch {
            try {
                val response = weatherRepository.getCurrentWeather(cityName, Constants.API_KEY)
                if (response.isSuccessful) {
                    _weatherData.postValue(response.body())
                }
                else {
                    _error.postValue("Error: ${response.message()}")
                }
            }
            catch (e: Exception) {
                _error.postValue("Error: ${e.message}")
            }
            finally {
                _isLoading.value = false
            }
        }
    }
}