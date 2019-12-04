package ru.skillbranch.gameofthrones.database

import androidx.room.TypeConverter

class StringListConverter {

    @TypeConverter
    fun fromStringToList(value: String): List<String> = value.split(";")

    @TypeConverter
    fun fromListToString(list: List<String>) = list.joinToString(";")

}