package com.example.weatherapp.data

// represents the JSON body we send to APIDog
// Gson converts this kotlin object into Json automaticly when we POST
data class FeedbackRequest(
    val city: String,
    val rating: Int,
    val comment: String
)
