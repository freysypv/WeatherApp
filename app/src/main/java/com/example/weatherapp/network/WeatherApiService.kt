package com.example.weatherapp.network
import com.example.weatherapp.data.WeatherResponse
import retrofit2.Response // wrapper class that caps the result and gives us issSuccessful + error codes
import retrofit2.http.GET // annotation for HTTP GET requests
import retrofit2.http.Query // annotation for query parameters in the URL( ?key = value)

// a contract / blueprint
// wee write WHAT calls we want, retrofit generate s How they actually work
interface WeatherApiService {
    // @GEt = make an HTTP request to the endpoint
    // @GET = make an HTTP GET request to: WEATHER_BASE_URL + "data/2.5/weather"
  // Full URL example: https://api.openweathermap.org/data/2.5/weather?q=London&appid=KEY&units=metric
    @GET("data/2.5/weather")

    suspend fun getWeather(
        @Query( "q")   city: String,  // city we search for
        @Query( "appid") apiKey: String, // API key, adds your API key
        @Query( "units") units: String  // , add units of measurement, imperial or metric / units of measurement, imperial or metric
    ): Response<WeatherResponse>

    //Response <WEAtherREsponse> = the result wrapper
    // .isSuccessfull = true if HTTP 200-299
    // .body() = the parsed WeatherResponse object(or null if failed)
    /// .code = the HTTp Status number (200, 404, 500)


}