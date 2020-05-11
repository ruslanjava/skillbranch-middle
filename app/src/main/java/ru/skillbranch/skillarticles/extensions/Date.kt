package ru.skillbranch.skillarticles.extensions

import java.text.SimpleDateFormat
import java.util.*

const val SECOND = 1000L
const val MINUTE = 60 * SECOND
const val HOUR = 60 * MINUTE
const val DAY = 24 * HOUR

fun Date.format(pattern: String = "HH:mm:ss dd.MM.yy"): String {
    val dateFormat = SimpleDateFormat(pattern, Locale.getDefault())
    TimeZone.setDefault(SimpleTimeZone(0, "My timezone"))
    return dateFormat.format(this)
}

fun Date.add(value:Int, units: TimeUnits = TimeUnits.SECOND) : Date {
    var time = this.time

    time += when (units) {
        TimeUnits.SECOND -> value * SECOND
        TimeUnits.MINUTE -> value * MINUTE
        TimeUnits.HOUR -> value * HOUR
        TimeUnits.DAY -> value * DAY
    }

    this.time = time
    return this
}

fun Date.shortFormat(): String {
    return format()
}

fun Date.humanizeDiff(date: Date = Date()): String {
    var newDate = Calendar.getInstance()
    newDate.time = date

    var oldDate = Calendar.getInstance()
    oldDate.time = this

    if (oldDate.before(newDate)) {
        if (oldDate.before(newDate) && oldDate.addField(Calendar.YEAR, 1).addField(Calendar.MILLISECOND, -1).before(newDate)) {
            return "более года назад"
        }

        // разбираем случаи, когда разница больше 1 дня
        var days = ((newDate.timeInMillis - oldDate.timeInMillis) / DAY).toInt()
        if (days > 0) {
            return "${TimeUnits.DAY.plural(days)} назад"
        }

        // когда разница больше 1 часа
        var hours = ((newDate.timeInMillis - oldDate.timeInMillis) / HOUR).toInt()
        if (hours > 0) {
            return "${TimeUnits.HOUR.plural(hours)} назад"
        }

        // когда разница больше 1 минуты
        var minutes = (((newDate.timeInMillis - oldDate.timeInMillis) / MINUTE)).toInt()
        if (minutes > 0) {
            return "${TimeUnits.MINUTE.plural(minutes)} назад"
        }

        var seconds = (((newDate.timeInMillis - oldDate.timeInMillis) / MINUTE)).toInt()
        if (seconds > 0) {
            return "несколько секунд назад"
        }

        if (seconds == 0) {
            return "только что"
        }
    }

    // если разница больше 1 года
    if (newDate.addField(Calendar.YEAR, 1).addField(Calendar.MILLISECOND, -1).before(oldDate)) {
        return "более чем через год"
    }

    // разбираем случаи, когда разница больше 1 дня
    var days = ((oldDate.timeInMillis - newDate.timeInMillis) / DAY).toInt()
    if (days > 0) {
        return "через ${TimeUnits.DAY.plural(days)}"
    }

    // когда разница в несколько часов
    var hours = ((oldDate.timeInMillis - newDate.timeInMillis) / HOUR).toInt()
    if (hours > 0) {
        return "через ${TimeUnits.HOUR.plural(hours)}"
    }

    // когда разница в несколько минут
    var minutes = (((oldDate.timeInMillis - newDate.timeInMillis) / MINUTE)).toInt()
    if (minutes > 0) {
        return "через ${TimeUnits.MINUTE.plural(minutes)}"
    }

    var seconds = (((oldDate.timeInMillis - newDate.timeInMillis) / SECOND)).toInt()
    if (seconds > 0) {
        return "через несколько секунд"
    }

    return "только что"
}

private fun Calendar.addField(field: Int, value: Int) : Calendar {
    val newCalendar = Calendar.getInstance()
    newCalendar.time = this.time
    newCalendar.add(field, value)
    return newCalendar
}