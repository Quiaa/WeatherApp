package com.example.weatherapp.ui.main

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.bumptech.glide.Glide
import com.example.weatherapp.R
import com.example.weatherapp.databinding.ActivityMainBinding
import com.example.weatherapp.util.Constants
import com.example.weatherapp.util.Resource
import dagger.hilt.android.AndroidEntryPoint
import com.example.weatherapp.data.db.WeatherEntity

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
                    updateUI(resource.data) // Pass WeatherEntity to updateUI
                }
                is Resource.Error -> {
                    binding.progressBar.visibility = View.GONE
                    // If there is an error, but we have old data, updateUI with it
                    updateUI(resource.data)
                    Toast.makeText(this, resource.message, Toast.LENGTH_LONG).show()
                }
                is Resource.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                }
            }
        }
    }
    private fun updateUI(weather: WeatherEntity?) {
        binding.tvCity.text = weather?.cityName ?: "..."
        binding.tvTemperature.text = weather?.temperature?.let { "%.0f°C".format(it) } ?: ""
        binding.tvDescription.text = weather?.description ?: ""

        weather?.iconCode?.let { iconCode ->
            if (iconCode.isNotEmpty()) {
                val iconUrl = "${Constants.BASE_IMAGE_URL}${iconCode}@2x.png"
                Glide.with(this).load(iconUrl).into(binding.ivWeatherIcon)
            } else {
                binding.ivWeatherIcon.setImageDrawable(null)
            }
        } ?: binding.ivWeatherIcon.setImageDrawable(null)
    }
}