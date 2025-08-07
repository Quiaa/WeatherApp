package com.example.weatherapp.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "forecast_data")
data class ForecastEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0, // Unique ID for each forecast entry
    val ownerCityName: String, // To link this forecast to a specific city
    val dt: Long,
    val temperature: Double,
    val description: String,
    val iconCode: String
)