package ru.skillbranch.skillarticles.extensions

fun List<Pair<Int, Int>>.groupByBounds(bounds: List<Pair<Int, Int>>): List<MutableList<Pair<Int, Int>>> {
    val result = mutableListOf<MutableList<Pair<Int, Int>>>()
    var index = 0
    bounds.forEach {
        val subList = mutableListOf<Pair<Int, Int>>()
        while (index < size) {
            val searchPair = get(index)
            if (searchPair.first >= it.first && searchPair.second <= it.second) {
                subList.add(searchPair)
                index++
            } else {
                break
            }
        }
        result.add(subList)
    }
    return result
}
