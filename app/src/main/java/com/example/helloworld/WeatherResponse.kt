package com.example.helloworld

data class WeatherResponse(
    val list: List<WeatherItem>
)
data class WeatherItem(
    val name: String,
    val main: WeatherMain,
    val weather: List<WeatherCondition>
)
data class WeatherMain(
    val temp: Double,
    val feels_like: Double,
    val temp_min: Double,
    val temp_max: Double,
    val pressure: Int,
    val humidity: Int
)
data class WeatherCondition(
    val main: String,
    val description: String,
    val icon: String
)



