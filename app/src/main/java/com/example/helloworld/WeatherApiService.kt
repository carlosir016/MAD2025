package com.example.helloworld

import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.Call

interface WeatherApiService {
    @GET("find")
    fun getWeatherForecast(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("cnt") count: Int = 10,
        @Query("APPID") apiKey: String
    ): Call<WeatherResponse>
}