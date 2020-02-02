package ru.skillbranch.skillarticles.extensions

fun String.indexesOf(query: String) : List<Int> {
    val result = mutableListOf<Int>()
    if (query.isEmpty()) {
        return result
    }

    var index = 0
    while (index < length && index != -1) {
        index = this.indexOf(query, startIndex = index, ignoreCase = true)
        if (index != -1) {
            result.add(index)
            index += query.length
        }
    }

    return result
}

