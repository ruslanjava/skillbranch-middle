package ru.skillbranch.skillarticles.extensions

fun String.indexesOf(query: String) : List<Int> {
    val result = mutableListOf<Int>()
    var index = 0
    while (index < length && index != -1) {
        index = this.indexOf(query, index)
        if (index != -1) {
            result.add(index)
        }
    }
    return result
}