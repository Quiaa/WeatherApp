package com.example.weatherapp.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.data.db.WeatherEntity
import com.example.weatherapp.data.repository.WeatherRepository
import com.example.weatherapp.util.Constants
import com.example.weatherapp.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@HiltViewModel
class MainViewModel @Inject constructor(
    private val weatherRepository: WeatherRepository
) : ViewModel() {

    // The LiveData now holds a Resource of WeatherEntity
    private val _weatherState = MutableLiveData<Resource<WeatherEntity>>()
    val weatherState: LiveData<Resource<WeatherEntity>> = _weatherState

    fun fetchWeather(cityName: String) {
        // The repository now returns a Flow. We collect it in the ViewModel.
        weatherRepository.getCurrentWeather(cityName.trim(), Constants.API_KEY)
            .onEach { result ->
                _weatherState.value = result
            }
            .launchIn(viewModelScope) // launchIn is a concise way to collect a flow in a scope.
    }
}