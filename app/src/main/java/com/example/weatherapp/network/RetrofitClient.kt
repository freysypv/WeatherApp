package com.example.weatherapp.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import android.content.Context
import okhttp3.Cache
import okhttp3.OkHttpClient


import okhttp3.Interceptor
import okhttp3.Response
import java.io.File
import java.util.concurrent.TimeUnit
import java.io.IOException


// retry interceptor: automatically retry a request if it fails
// this is a class that implements the Interceptor interface
class RetryInterceptor(private val maxRetries: Int) : Interceptor {
    override fun intercept (chain: Interceptor.Chain): Response {
        // chain = the request pipeline - calling cai.rprocced
        var lastException: Exception? = null
        repeat ( maxRetries) {
            try {
                return chain.proceed( chain.request()) //attempt the request, the network call
            } catch (e: Exception) {                            // if successful: 'return' the response
                lastException = e                               // IoException = network failure

            }
        }
        throw lastException ?: IOException("Rest failed after $maxRetries retries")
    }
}

object RetrofitClient {
    //  late-init = will be assigned in init() before any service is accessed
    private lateinit var appContext: Context

    private lateinit var cache: Cache


    fun init(context: Context) {
        appContext = context.applicationContext
        // .applicationContext = the global app context, not tied to any specific activity
        // once creates, the same instance is reused - no rebuilding
    }

    fun clearCache() {
        if(::cache.isInitialized) {
            cache.evictAll()
        }
    }

    // By lazy - create this only when it is first accessed
    // If weatherApiService is never used, it's never created - save memory
    // Once creates, the same instance is reused - no rebuilding
    val weatherApiService: WeatherApiService by lazy {
        val cacheDir = File(appContext.cacheDir, "weather_cache")
        // appContext.cashDir = the apps private cache directory on dick
        // "http_cache = subfolder name we choose - keeps HTTP cache separate

        val cache = Cache(cacheDir, 10 * 1024 * 1024)
        // %L * 1024*1024 = 5MB cache size disc space: l= long type

        val okHTTPClients = OkHttpClient.Builder()
            .cache(cache) // tell okHTTP to use disk cache for the GET request
            .addNetworkInterceptor(){ chain -> // Network interceptor runs After the network response is received
                chain.proceed(chain.request()).newBuilder()
                    .header("Cache-Control", "public, max-age=300") // cache valid for 300 sec(5m)
                    .build()
            }
            .addInterceptor(RetryInterceptor(3)) // add retry interceptor to the client/ Retry up to 3 times
            .connectTimeout(30, TimeUnit.SECONDS) // timeout after 30 sec = max time to stable the tcp connection to the server
            .readTimeout(30, TimeUnit.SECONDS) // timeout after 30 sec = max time to read the response from the server/  max time wait for data from the server
            .build()


        Retrofit.Builder()
            .baseUrl(AppConstants.WEATHER_BASE_URL)
            .client(okHTTPClients)
            .addConverterFactory(GsonConverterFactory.create())
            // automatic JSON -to- data - class conversion when the API
            //return JSON, Gson maps it to the weatherResponse data class
            .build()
            .create(WeatherApiService::class.java)
            // retrofit generates the real HTTP implementation for us
            // tells retrofit which interface to implements and what converter to use

    }


    val feedbackApiService: FeedbackApiService by lazy {
        val okHTTPClients = OkHttpClient.Builder()

            .addInterceptor(RetryInterceptor(3)) // add retry interceptor to the client/ Retry up to 3 times
            .connectTimeout(30, TimeUnit.SECONDS) // timeout after 30 sec = max time to stable the tcp connection to the server
            .readTimeout(30, TimeUnit.SECONDS) // timeout after 30 sec = max time to read the response from the server/  max time wait for data from the server


        Retrofit.Builder()
            .baseUrl(AppConstants.FEEDBACK_BASE_URL)
            .client(OkHttpClient())
            .addConverterFactory(GsonConverterFactory.create())
            // automatic JSON -to- data - class conversion when the API
            //return JSON, Gson maps it to the weatherResponse data class
            .build()
            .create(FeedbackApiService::class.java)
        // retrofit generates the real HTTP implementation for us
        // tells retrofit which interface to implements and what converter to use

    }

}