package com.example.weatherapp.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.data.db.ForecastEntity
import com.example.weatherapp.data.db.WeatherEntity
import com.example.weatherapp.data.repository.WeatherRepository
import com.example.weatherapp.util.Constants
import com.example.weatherapp.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import javax.inject.Inject
import kotlinx.coroutines.launch
import kotlinx.coroutines.ExperimentalCoroutinesApi


@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: WeatherRepository
) : ViewModel() {

    private val _activeCityName = MutableStateFlow("Istanbul")
    val activeCityName = _activeCityName.asStateFlow()
    val weatherState: LiveData<Resource<WeatherEntity>> = _activeCityName.flatMapLatest { city ->
        repository.getCurrentWeather(city, Constants.API_KEY)
    }.asLiveData()

    val forecastState: LiveData<Resource<List<ForecastEntity>>> = _activeCityName.flatMapLatest { city ->
        repository.getForecast(city, Constants.API_KEY)
    }.asLiveData()

    fun setCity(cityName: String) {
        if (cityName.isNotBlank() && _activeCityName.value.equals(cityName, ignoreCase = true).not()) {
            _activeCityName.value = cityName
        }
    }

    fun loadWeatherForCurrentLocation() {
        viewModelScope.launch {
            val result = repository.fetchCityNameForCurrentLocation()
            if (result is Resource.Success) {
                result.data?.let { cityName ->
                    setCity(cityName)
                }
            } else if (result is Resource.Error) {
            }
        }
    }
}