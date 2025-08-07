package com.example.weatherapp.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.data.model.WeatherResponse
import com.example.weatherapp.data.repository.WeatherRepository
import com.example.weatherapp.util.Constants
import com.example.weatherapp.util.Resource
import kotlinx.coroutines.launch
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(private val weatherRepository: WeatherRepository) : ViewModel() {
    private val _weatherState = MutableLiveData<Resource<WeatherResponse>>()
    val weatherState: LiveData<Resource<WeatherResponse>> = _weatherState

    fun fetchWeather(cityName: String) {
        // Start by posting the Loading state
        _weatherState.postValue(Resource.Loading())

        viewModelScope.launch {
            // The repository now handles the try-catch and response parsing.
            val result = weatherRepository.getCurrentWeather(cityName, Constants.API_KEY)
            _weatherState.postValue(result)
        }
    }
}