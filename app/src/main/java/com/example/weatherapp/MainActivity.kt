package com.example.weatherapp

// Class 1

// class 2

// Class 3

// Class 4
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.weatherapp.data.FeedbackRequest
import com.example.weatherapp.network.AppConstants
import com.example.weatherapp.network.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        RetrofitClient.init(context = this)
        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier
                        .fillMaxSize(),
                ) {
                    WeatherScreen()
                }
            }
        }
    }
}

@Composable
fun WeatherScreen() {
    // a value compose watches changes for
    var city by remember { mutableStateOf("") }
    var cityResult by remember { mutableStateOf("City: --") }
    var tempResult by remember { mutableStateOf("Temperature: --") }
    var descResult by remember { mutableStateOf("Description: --") }


    var windResult by remember { mutableStateOf("Wind speed: --") }
    var humidityResult by remember { mutableStateOf("Humidity: --") }
    var isLoading by remember { mutableStateOf(false)}

    var currentCity by remember { mutableStateOf("") }
    var rating by remember { mutableStateOf(3) }
    var comment by remember { mutableStateOf("")}
    var feedbackResult by remember { mutableStateOf("") }
    var feedbackLoading by remember { mutableStateOf(false) }

    var recentSearches by remember { mutableStateOf(listOf<String>()) }

    val context = LocalContext.current // Toast.makeText() requires an Android Context -- a composable isn't


    val scope = rememberCoroutineScope()
    // three state variables: city, cityResult, and isLoading



    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        TextField(
            value = city,
            onValueChange = { city = it },
            label = { Text("Enter city name") },
            modifier = Modifier
                .fillMaxWidth()
        )
        // Button to fetch weather
        Button(
            enabled = !isLoading, // Disable the button while loading mid-request
            onClick = {
                val trimmedCity = city.trim()
                if (trimmedCity.isEmpty()) {
                    Toast.makeText(context, "Please enter a city name", Toast.LENGTH_SHORT).show()
                } else {
                    isLoading = true

                    // Assignment 4
                    // 1. Declare "var feedbackLoading by remember { mutableStateOf(false) }"
                    //    near the top of WeatherScreen(), next to the other state variables
                    // 2. Set feedbackLoading = true right before the feedback scope.launch { }
                    // 3. Set feedbackLoading = false inside a finally { } wrapped around the
                    //    feedback try/catch -- same shape as Get Weather's finally block
                    // 4. On the Submit Feedback Button: add enabled = !feedbackLoading,
                    //    and swap its Text to "Submitting..." while feedbackLoading is true

                    feedbackLoading = true
                    scope.launch {
                        try {
                            val response = withContext(Dispatchers.IO) {
                                RetrofitClient.weatherApiService.getWeather(
                                    city = trimmedCity,
                                    apiKey = AppConstants.API_KEY,
                                    units = AppConstants.UNITS
                                )
                            }

                            // response. row() = HTTP response/ gives underlying response
                            Log.d("WeatherApp", "request URL: ${response.raw().request.url}")
                            // the exact URL Retrofit built.check log cat filter by WeatherApp
                            Log.d("WeatherApp", "response code: ${response.code()}")

                            if (response.isSuccessful) {
                                val weather = response.body()
                                if (weather != null) {
                                    cityResult = "City: ${weather.name}"
                                    tempResult = "Temperature: ${weather.main.temp}°F"
                                    descResult = "Description: ${weather.weather[0].description}"
                                    windResult = "Wind speed: ${weather.wind.speed} m/s"
                                    humidityResult = "Humidity: ${weather.main.humidity}%"
                                    currentCity = trimmedCity // remember this city so feedback can post
                                    recentSearches = (listOf(weather.name) +recentSearches.filter { it != weather.name}).take(n = 5)



                                    // Assignment 2
                                    // 1. Set windResult from weather.wind.speed (append " m/s")
                                    // 2. Set humidityResult from weather.main.humidity (append "%")
                                    // 3. Below in the else branch, replace the single Toast with a
                                    // when (response.code()) -- see Hour 3 for the exact messages
                                    // TODO: this Toast gets replaced by step 3 above -- a when (response.code())
                                    // with different messages for 404, 401, and anything else
                                }
                            } else {
                                when (response.code()) {
                                    404 -> Toast.makeText(context, "City not found", Toast.LENGTH_SHORT).show()
                                    401 -> Toast.makeText(context, "Unauthorized", Toast.LENGTH_SHORT).show()
                                }
                                // Handle error response here
                                //Toast.makeText(context, "City not found. Check the name and try again.", Toast.LENGTH_SHORT).show()
                            }
                            // Handle network errors here
                        } catch (e: Exception) {
                            Toast.makeText(context, "Network error. Check your connection.", Toast.LENGTH_SHORT).show()
                        }
                        finally {
                            // Reset the loading state after the request
                            //finally always runs - success, HTTP error, or exception
                            isLoading = false
                            feedbackLoading = false
                        }

                    }


                    // Toast.makeText(context, "Coming soon -- weather for $trimmedCity", Toast.LENGTH_SHORT).show()
                    // DELETE the line above when you implement the real fetch
                }

            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
        ) {
            Text(if (isLoading) "Loading..." else "Get Weather")
        }
        Text(cityResult, fontSize = 20.sp, modifier = Modifier.padding(top =24.dp),

        )
        Text(
            tempResult, fontSize = 18.sp, modifier = Modifier.padding(top =8.dp),
        )
        Text(
            descResult, fontSize = 18.sp, modifier = Modifier.padding(top =8.dp),

        )
        Text(
            windResult, fontSize = 18.sp, modifier = Modifier.padding(top =8.dp),

            )
        Text(
            humidityResult, fontSize = 18.sp, modifier = Modifier.padding(top =8.dp),

            )
        // Cache control + recent searches
        OutlinedButton(onClick = {
            RetrofitClient.clearCache()
            Toast.makeText(context, "Cache cleared", Toast.LENGTH_SHORT).show()
        },
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp)){Text("Clear Cache")}

        if (recentSearches.isNotEmpty()) {
            Text("Recent Searches", fontSize = 14.sp, modifier = Modifier.padding(top = 16.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(top = 4.dp)
            ) {
                recentSearches.forEach { recentCity ->
                    AssistChip(
                        onClick = { city = recentCity },
                        label = { Text(recentCity) },
                        modifier = Modifier.padding(end = 8.dp)
                    )
                }
            }
        }


        HorizontalDivider(modifier = Modifier.padding(top = 24.dp, bottom = 16.dp))

        Text("how do you feel about today's weather", fontSize = 16.sp)

        // rating system
        Slider(
            value = rating.toFloat(),
            onValueChange = { rating = it.toInt() },
            valueRange = 1f..5f,
            steps = 3,
        )

        Text("Rating: $rating/5") // slider has no built-in label

        TextField(
            value = comment,
            onValueChange = { comment = it }, // whatever the user typed, update the comment variable
            label = { Text("Leave a comment about the weather...") },
            minLines = 3,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
        )

        Button(
            onClick = {
                if (currentCity.isEmpty()) {
                    Toast.makeText(context, "Please fetch weather for a city first", Toast.LENGTH_SHORT).show()
                } else if (comment.isBlank()) {
                    Toast.makeText(context, "Please leave a comment", Toast.LENGTH_SHORT).show()
                } else {
                    scope.launch {
                        try {
                            val request = FeedbackRequest(city = currentCity, rating = rating, comment = comment)

                            val response = withContext(Dispatchers.IO) {
                                RetrofitClient.feedbackApiService.submitFeedback(request)
                            }
                            // ASSIGNMENT 3
                            // 1. If response.isSuccessful: set feedbackResult to a success message,
                            //    then clear the comment field and reset rating back to 3
                            // 2. If NOT successful: set feedbackResult to a failure message
                            if (response.isSuccessful) {
                                feedbackResult = "Feedback submitted successfully!"
                                comment = ""
                                rating = 3
                            } else {
                                feedbackResult = "Feedback submission failed. Please try again."
                            }


                        } catch (e: Exception) {
                            Toast.makeText(context, "Error submitting feedback. Check your connection", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),

            !feedbackLoading
        ) {
            Text(if (feedbackLoading) "Submitting" else "Submit Feedback")
        }
        Text(
            feedbackResult, fontSize = 18.sp, modifier = Modifier.padding(top = 8.dp)
        )
    }
}