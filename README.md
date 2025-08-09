# WeatherApp

A simple weather application for Android that shows the current weather and a 5-day forecast.

## Features

*   **Current Weather:** Displays the current temperature, description, and weather icon for your location or a searched city.
*   **5-Day Forecast:** Provides a 5-day weather forecast.
*   **Search:** Allows you to search for the weather in any city.
*   **Location-Based:** Automatically fetches weather for your current location on startup.

## Tech Stack

*   **Language:** Kotlin
*   **Architecture:** MVVM (Model-View-ViewModel)
*   **UI:** Android Jetpack, DataBinding
*   **Asynchronous Programming:** Coroutines
*   **Networking:** Retrofit
*   **Dependency Injection:** Hilt
*   **Database:** Room for caching
*   **Image Loading:** Glide
*   **Location:** Google Play Services

## Setup

1.  **Clone the repository:**
    ```bash
    git clone https://github.com/Quiaa/WeatherApp.git
    ```
2.  **Get an API Key:**
    - Go to [OpenWeatherMap](https://openweathermap.org/api) and create an account.
    - Subscribe to the "5 Day / 3 Hour Forecast" API, which is free.
    - Generate an API key.

3.  **Add the API Key:**
    - Open the file `app/src/main/java/com/example/weatherapp/util/Constants.kt`.
    - Replace `"YOUR_OPENWEATHER_API_KEY"` with your actual OpenWeatherMap API key.

    ```kotlin
    object Constants {
        const val API_KEY = "YOUR_OPENWEATHER_API_KEY" // <-- PASTE YOUR KEY HERE
        // ...
    }
    ```

4.  **Build and Run:**
    - Open the project in Android Studio.
    - Build and run the application on an emulator or a physical device.

## Screenshots


| Current Weather |
| :-------------: |
| <img width="423" height="887" alt="image" src="https://github.com/user-attachments/assets/d642d1a0-f6b5-45ca-8d44-b943aa20c3a9" />
     
