package com.example.weatherapp.network

// "object" in Kotlin = a singleton class
// A singleton means there is exactly ONE instance in the whole app
// You never write: val c = AppConstants() -- you just use: AppConstants.API_KEY
// Perfect for constants because you never want two different copies of your API key
object AppConstants {
    // Base URL = the root address of the API
    // Every call start with this , then adds the specific endpoint path
    // important: must end with "/" retrofit requires this
    const val WEATHER_BASE_URL = "https://api.openweathermap.org/"

    // MY API KEY-
    const val API_KEY = "ab53d345afe18e3ed317bcd91a9e6162"

    // units
    const val UNITS = "imperial"

    // Feedback base URL
    // NEW for Class 3: Paste your APIDog Cloud Mock base URL here
    // Example: "https://mock.apidog.com/m1/123456-0-default/"
    // IMPORTANT: must end with "/" -- Retrofit requires it

    const val FEEDBACK_BASE_URL = "https://mock.apidog.com/m1/1407478-1417570-default/"
}