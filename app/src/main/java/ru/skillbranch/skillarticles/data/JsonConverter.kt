package ru.skillbranch.skillarticles.data

import com.squareup.moshi.FromJson
import com.squareup.moshi.Moshi
import com.squareup.moshi.ToJson
import java.util.*

object JsonConverter {

    val moshi = Moshi.Builder()
        .add(DateAdapter())
        .build()

}

class DateAdapter {

    @FromJson
    fun fromJson(timestamp: Long) = Date(timestamp)

    @ToJson
    fun toJson(date: Date) = date.time

}