package com.example.weatherapp.network

// "object" in Kotlin = a singleton class
// A singleton means there is exactly ONE instance in the whole app
// You never write: val c = AppConstants() -- you just use: AppConstants.API_KEY
// Perfect for constants because you never want two different copies of your API key
object AppConstants {
    // Base URL = the root address of the API
    // Every call start with this , then adds the specific endpoint path
    // important: must end with "/" retrofit requires this
    const val WEATHER_BASE_URL = "http:api.openweathermap.org/"

    // MY API KEY-
    const val API_KEY = "your api key here"

    // units
    const val UNITS = "imperial"
}