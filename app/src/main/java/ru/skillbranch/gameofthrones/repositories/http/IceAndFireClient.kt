package ru.skillbranch.gameofthrones.repositories.http

import kotlinx.coroutines.*
import ru.skillbranch.gameofthrones.data.remote.res.CharacterRes
import ru.skillbranch.gameofthrones.data.remote.res.HouseRes
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList
import kotlin.collections.HashSet

object IceAndFireClient {

    val service = IceAndFireServiceFactory.instance
    val scope = CoroutineScope(Dispatchers.IO)

    private const val THREADS = 12
    private const val PAGE_SIZE = THREADS * 5

    fun getAllHouses(): List<HouseRes> {
        val lock = Any()
        // "сырой" результат, который может содержать дубли. Сырой список нужен, чтобы
        // минимизировать время нахождения списка внутри блокировки
        val rawResult = ArrayList<HouseRes>()

        val start = CountDownLatch(1)
        val finish = CountDownLatch(THREADS)

        for (offset in 0..THREADS) {
            scope.async {
                start.await()
                try {
                    var page = offset
                    while (true) {
                        val pageHouses = service.houses(page, PAGE_SIZE)
                        if (pageHouses.isEmpty()) {
                            break
                        }
                        synchronized(lock) {
                            rawResult.addAll(pageHouses)
                        }
                        page += THREADS
                    }
                } finally {
                    finish.countDown()
                }
            }
        }
        start.countDown()
        finish.await(30, TimeUnit.SECONDS)

        // окончательный результат, который можно не спеша отфильтровать в одном потоке
        // без блокировки
        val result = ArrayList<HouseRes>()
        val processedLinks = HashSet<String>()
        rawResult.forEach {
            if (!processedLinks.contains(it.url)) {
                processedLinks.add(it.url)
                result.add(it)
            }
        }
        return result
    }

    fun getNeededHouses(houseNames: Array<out String>): List<HouseRes> {
        val hashedHouses = HashSet<String>()
        houseNames.forEach {
            hashedHouses.add(it)
        }

        val houses = getAllHouses()

        val result = mutableListOf<HouseRes>()
        for (house in houses) {
            if (hashedHouses.contains(house.name)) {
                result.add(house)
            }
        }
        return result
    }

    fun getNeedHousesWithCharacters(houseNames: Array<out String>): List<Pair<HouseRes, List<CharacterRes>>> {
        val result = ArrayList<Pair<HouseRes, List<CharacterRes>>>()
        val resultLock = Any()

        val houses = getNeededHouses(houseNames)

        val start = CountDownLatch(1)
        val finish = CountDownLatch(houseNames.size)

        houses.forEach {
            val house = it
            val houseMembers = house.swornMembers

            scope.async {
                start.await()
                try {
                    val ids = mutableListOf<String>()
                    houseMembers.forEach { url ->
                        val id = url.substringAfterLast("/")
                        ids.add(id)
                    }
                    val characters = loadCharacters(ids)
                    synchronized(resultLock) {
                        result.add(house to characters)
                    }
                } finally {
                    finish.countDown()
                }
            }
        }

        start.countDown()
        finish.await(30, TimeUnit.SECONDS)

        return result;
    }

    private fun loadCharacters(ids : List<String>) : List<CharacterRes> {
        val result = ArrayList<CharacterRes>()

        val start = CountDownLatch(1)
        val finish = CountDownLatch(ids.size)

        val lock = Any()
        ids.forEach {
            scope.async {
                start.await()
                try {
                    val character = service.character(it)
                    synchronized(lock) {
                        result.add(character)
                    }
                } finally {
                    finish.countDown()
                }
            }
        }

        start.countDown()
        finish.await(30, TimeUnit.SECONDS)

        return result
    }

}

