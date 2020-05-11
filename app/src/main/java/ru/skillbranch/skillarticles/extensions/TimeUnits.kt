package ru.skillbranch.skillarticles.extensions

enum class TimeUnits {

    SECOND {
        override fun plural(value : Int) : String {
            return plural(value, arrayOf("секунду", "секунды", "секунд"))
        }
    },

    MINUTE {
        override fun plural(value : Int) : String {
            return plural(value, arrayOf("минуту", "минуты", "минут"))
        }
    },

    HOUR {
        override fun plural(value : Int) : String {
            return plural(value, arrayOf("час", "часа", "часов"))
        }
    },

    DAY {
        override fun plural(value : Int) : String {
            return plural(value, arrayOf("день", "дня", "дней"))
        }
    };

    abstract fun plural(value : Int) : String

}

private fun plural(value : Int, variants : Array<String>) : String {
    return when {
        value % 10 == 1 && (value % 100 < 10 || value % 100 > 20) -> "$value ${variants[0]}"
        value % 10 in 2..4 -> "$value ${variants[1]}"
        else -> "$value ${variants[2]}"
    }
}