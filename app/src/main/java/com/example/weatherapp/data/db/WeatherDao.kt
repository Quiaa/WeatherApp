package com.example.weatherapp.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface WeatherDao {
    // If we insert weather data for a city that already exists, it will be replaced.
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWeather(weather: WeatherEntity)

    // Using Flow, this function will automatically emit new data whenever
    // the weather_data table changes for the given city. This makes the UI reactive.
    @Query("SELECT * FROM weather_data WHERE cityName = :cityName")
    fun getWeatherByCity(cityName: String): Flow<WeatherEntity?>
}