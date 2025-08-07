package com.example.weatherapp

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

// This annotation triggers Hilt's code generation, including a base class for your application
// that serves as the application-level dependency container.
@HiltAndroidApp
class WeatherApplication : Application() {
}