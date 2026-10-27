package com.example.weatherapp.data

// "data" sub-package -- all data model files live here

// data class = a Kotlin class designed only to hold data
// Kotlin auto-generates equals(), hashCode(), toString(), and copy() for free
// You never need to write: "if (a.name == b.name && a.main == b.main ...)" -- data class handles it

data class WeatherResponse(
val name: String,                   // city name - matching "the city name" in the JSON response
val main: Main,                     // main weather info - matching "the main weather info" in the JSON response
    val weather: List<Weather>,      // array of weather condition - matching "the weather condition" in the JSON response
    val wind: Wind                  // nested object -- contains wind speed and direction - matching "the wind speed and direction" in the JSON response

)

// maps to the "main inside Json"
data class Main(
    val temp: Double,  // current temperature
    val humidity: Int // humidity as percentage
)

data class Weather(
    val description: String // weather description. " clear sky, "cloudy",ect....
)

data class Wind(
    val speed: Double, // wind speed in meter per second
)