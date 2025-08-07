package com.example.weatherapp.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.data.db.ForecastEntity
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

    private val _weatherState = MutableLiveData<Resource<WeatherEntity>>()
    val weatherState: LiveData<Resource<WeatherEntity>> = _weatherState
    private val _forecastState = MutableLiveData<Resource<List<ForecastEntity>>>()
    val forecastState: LiveData<Resource<List<ForecastEntity>>> = _forecastState

    fun fetchWeather(cityName: String) {
        weatherRepository.getCurrentWeather(cityName.trim(), Constants.API_KEY)
            .onEach { result -> _weatherState.value = result }
            .launchIn(viewModelScope)

        weatherRepository.getForecast(cityName.trim(), Constants.API_KEY)
            .onEach { result -> _forecastState.value = result }
            .launchIn(viewModelScope)
    }
}