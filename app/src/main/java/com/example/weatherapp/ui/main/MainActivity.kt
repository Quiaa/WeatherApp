package com.example.weatherapp.ui.main

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.weatherapp.R
import com.example.weatherapp.databinding.ActivityMainBinding
import com.example.weatherapp.util.Constants
import com.example.weatherapp.util.Resource
import dagger.hilt.android.AndroidEntryPoint
import com.example.weatherapp.data.db.WeatherEntity
import com.example.weatherapp.ui.adapter.ForecastAdapter

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var forecastAdapter: ForecastAdapter
    private val viewModel: MainViewModel by viewModels()

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                viewModel.loadWeatherForCurrentLocation()
            } else {
                Toast.makeText(this, "Location permission is required for automatic weather.", Toast.LENGTH_LONG).show()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        binding.lifecycleOwner = this

        setupRecyclerView()
        checkAndRequestLocationPermission()

        observeWeatherState()
        observeForecastState()

        binding.btnSearch.setOnClickListener {
            val cityName = binding.etCityName.text.toString()
            viewModel.setCity(cityName)
        }
    }
    private fun checkAndRequestLocationPermission() {
        when {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED -> {
                viewModel.loadWeatherForCurrentLocation()
            }
            else -> {
                requestPermissionLauncher.launch(Manifest.permission.ACCESS_COARSE_LOCATION)
            }
        }
    }

    private fun setupRecyclerView() {
        forecastAdapter = ForecastAdapter()
        binding.rvForecast.apply {
            adapter = forecastAdapter
            layoutManager = LinearLayoutManager(this@MainActivity, LinearLayoutManager.HORIZONTAL, false)
        }
    }

    private fun observeWeatherState() {
        viewModel.weatherState.observe(this) { resource ->
            when (resource) {
                is Resource.Success -> {
                    binding.progressBar.visibility = View.GONE
                    updateUI(resource.data)
                }
                is Resource.Error -> {
                    binding.progressBar.visibility = View.GONE
                    updateUI(resource.data)
                    Toast.makeText(this, resource.message, Toast.LENGTH_LONG).show()
                }
                is Resource.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                }
            }
        }
    }
    private fun observeForecastState() {
        viewModel.forecastState.observe(this) { resource ->
            when (resource) {
                is Resource.Success -> {
                    binding.progressBar.visibility = View.GONE
                    forecastAdapter.submitList(resource.data)
                }
                is Resource.Error -> {
                    forecastAdapter.submitList(resource.data ?: emptyList())
                    Toast.makeText(this, resource.message, Toast.LENGTH_SHORT).show()
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