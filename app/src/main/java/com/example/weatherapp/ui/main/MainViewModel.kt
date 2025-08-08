package com.example.weatherapp.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asFlow
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
import kotlinx.coroutines.flow.combine
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import androidx.lifecycle.map


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

    private val _selectedDate = MutableStateFlow(LocalDate.now())
    val selectedDate = _selectedDate.asStateFlow()

    val filteredForecastState: LiveData<Resource<List<ForecastEntity>>> =
        forecastState.asFlow().combine(_selectedDate) { resource, date ->
            // This block runs automatically whenever forecastState or _selectedDate changes.
            if (resource is Resource.Success) {
                val filteredList = resource.data?.filter { forecastEntity ->
                    val forecastDate = Instant.ofEpochSecond(forecastEntity.dt)
                        .atZone(ZoneId.systemDefault())
                        .toLocalDate()
                    forecastDate.isEqual(date)
                }
                Resource.Success(filteredList ?: emptyList())
            } else {
                // Pass the Loading or Error states directly.
                resource
            }
        }.asLiveData()

    fun selectDate(date: LocalDate) {
        _selectedDate.value = date
    }

    fun setCity(cityName: String) {
        if (cityName.isNotBlank() && _activeCityName.value.equals(cityName, ignoreCase = true).not()) {
            _activeCityName.value = cityName
            _selectedDate.value = LocalDate.now() // THE FIX: Reset the selected date to today whenever the city changes.
        }
    }

    val uniqueDaysState: LiveData<List<LocalDate>> = forecastState.map { resource ->
        if (resource is Resource.Success) {
            resource.data?.map {
                Instant.ofEpochSecond(it.dt).atZone(ZoneId.systemDefault()).toLocalDate()
            }?.distinct() ?: emptyList()
        } else {
            emptyList()
        }
    }

    val isLoading = MediatorLiveData<Boolean>().apply {
        addSource(weatherState) { value = it is Resource.Loading }
        addSource(filteredForecastState) { value = it is Resource.Loading }
    }

    fun loadWeatherForCurrentLocation() {
        viewModelScope.launch {
            val result = repository.fetchCityNameForCurrentLocation()
            if (result is Resource.Success) {
                result.data?.let { cityName ->
                    setCity(cityName)
                }
            } else if (result is Resource.Error) {
                // An event mechanism may be added here in the future
                // to notify the user in case of an error.
            }
        }
    }
}