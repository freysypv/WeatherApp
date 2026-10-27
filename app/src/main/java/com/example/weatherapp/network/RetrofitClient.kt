package com.example.weatherapp.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    // By lazy - create this only when it is first accessed
    // If weatherApiService is never used, it's never created - save memory
    // Once creates, the same instance is reused - no rebuilding
    val weatherApiService: WeatherApiService by lazy {
        Retrofit.Builder()
            .baseUrl(AppConstants.WEATHER_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            // automatic JSON -to- data - class conversion when the API
            //return JSON, Gson maps it to the weatherResponse data class
            .build()
            .create(WeatherApiService::class.java)
            // retrofit generates the real HTTP implementation for us
            // tells retrofit which interface to implements and what converter to use

    }


}