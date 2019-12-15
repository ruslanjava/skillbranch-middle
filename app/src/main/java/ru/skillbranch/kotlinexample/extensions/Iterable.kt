package ru.skillbranch.kotlinexample.extensions

fun <T> List<T>.dropLastUntil(predicate: (T) -> Boolean): List<T> {
    val result = mutableListOf<T>()
    run loop@ {
        forEach {
            if (predicate.invoke(it)) {
                return@loop
            }
            result.add(it)
        }
    }
    return result
}
