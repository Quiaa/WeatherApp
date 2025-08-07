package com.example.weatherapp.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ForecastDao {
    // Insert a list of forecast items. Replace on conflict.
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertForecasts(forecasts: List<ForecastEntity>)

    // Delete old forecasts for a specific city before inserting new ones.
    @Query("DELETE FROM forecast_data WHERE ownerCityName = :cityName")
    suspend fun deleteForecastsByCity(cityName: String)

    // Get all forecasts for a specific city, ordered by time.
    @Query("SELECT * FROM forecast_data WHERE ownerCityName = :cityName ORDER BY dt ASC")
    fun getForecastsByCity(cityName: String): Flow<List<ForecastEntity>>
}