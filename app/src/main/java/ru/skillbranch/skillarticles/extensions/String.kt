package ru.skillbranch.skillarticles.extensions

fun String?.indexesOf(query: String, ignoreCase: Boolean = true) : List<Int> {
    val result = mutableListOf<Int>()

    if (this == null || query.isEmpty()) {
        return result
    }

    var index = 0
    while (index < length && index != -1) {
        index = indexOf(query, startIndex = index, ignoreCase = ignoreCase)
        if (index != -1) {
            result.add(index)
            index += query.length
        }
    }

    return result
}

