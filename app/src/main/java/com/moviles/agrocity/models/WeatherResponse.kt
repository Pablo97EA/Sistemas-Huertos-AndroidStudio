package com.moviles.agrocity.models

data class WeatherResponse(
    val location: Location,
    val current: Current
)

data class Location(
    val name: String,
    val region: String,
    val country: String,
    val lat: Double,
    val lon: Double,
    val tz_id: String,
    val localtime_epoch: Long,
    val localtime: String
)

data class Current(
    val last_updated_epoch: Long,
    val last_updated: String,
    val temp_c: Double,
    val is_day: Int,
    val condition: Condition,
    val wind_kph: Double? = null,
    val humidity: Int? = null,
    val cloud: Int? = null,
    val feelslike_c: Double? = null,
    val uv: Double? = null
)


data class Condition(
    val text: String,
    val icon: String,
    val code: Int
)
