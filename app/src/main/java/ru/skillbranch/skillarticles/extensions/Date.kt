package ru.skillbranch.skillarticles.extensions

import java.text.SimpleDateFormat
import java.util.*

fun Date.format(pattern: String = "HH:mm:ss dd.MM.yy"): String {
    val dateFormat = SimpleDateFormat(pattern, Locale.getDefault())
    TimeZone.setDefault(SimpleTimeZone(0, "My timezone"))
    return dateFormat.format(this)
}