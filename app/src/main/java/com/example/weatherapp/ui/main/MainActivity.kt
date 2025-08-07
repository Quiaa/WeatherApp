package com.example.weatherapp.ui.main

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.bumptech.glide.Glide
import com.example.weatherapp.R
import com.example.weatherapp.data.model.WeatherResponse
import com.example.weatherapp.databinding.ActivityMainBinding
import com.example.weatherapp.util.Constants
import com.example.weatherapp.util.Resource
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        observeWeatherState()

        binding.btnSearch.setOnClickListener {
            val cityName = binding.etCityName.text.toString()
            if (cityName.isNotEmpty()) {
                viewModel.fetchWeather(cityName)
            } else {
                Toast.makeText(this, "Please enter a city name", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun observeWeatherState() {
        viewModel.weatherState.observe(this) { resource ->
            when (resource) {
                is Resource.Success -> {
                    binding.progressBar.visibility = View.GONE
                    // Update the UI with the successful data
                    updateUI(resource.data)
                }
                is Resource.Error -> {
                    binding.progressBar.visibility = View.GONE
                    updateUI(null) // Clear previous data
                    Toast.makeText(this, resource.message, Toast.LENGTH_LONG).show()
                }
                is Resource.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                }
            }
        }
    }
    private fun updateUI(weather: WeatherResponse?) {
        // Since we are using Data Binding, we can pass the data to the XML.
        // For Glide, we still need to handle it programmatically.
        binding.tvCity.text = weather?.name ?: "..."
        binding.tvTemperature.text = weather?.main?.temp?.let { "%.0f°C".format(it) } ?: ""
        binding.tvDescription.text = weather?.weather?.firstOrNull()?.description ?: ""

        weather?.weather?.firstOrNull()?.icon?.let { iconCode ->
            val iconUrl = "${Constants.BASE_IMAGE_URL}${iconCode}@2x.png"
            Glide.with(this).load(iconUrl).into(binding.ivWeatherIcon)
        } ?: binding.ivWeatherIcon.setImageDrawable(null) // Clear image if data is null
    }
}