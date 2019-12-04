package ru.skillbranch.gameofthrones.http

import ru.skillbranch.gameofthrones.data.remote.res.CharacterRes
import ru.skillbranch.gameofthrones.data.remote.res.HouseRes
import java.util.concurrent.CountDownLatch
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList
import kotlin.collections.HashSet

object IceAndFireClient {

    // значения получены эмпирическим путем
    private const val THREADS = 12
    private const val PAGE_SIZE = THREADS * 5
    private const val THREAD_POOL_SIZE = 180

    private val service = IceAndFireServiceFactory.newInstance()

    fun getAllHouses(): List<HouseRes> {
        val lock = Any()
        // "сырой" результат, который может содержать дубли. Сырой список нужен, чтобы
        // минимизировать время нахождения списка внутри блокировки
        val rawResult = ArrayList<HouseRes>()

        val start = CountDownLatch(1)
        val finish = CountDownLatch(THREADS)

        val executorService = Executors.newFixedThreadPool(THREAD_POOL_SIZE)
        for (offset in 0..THREADS) {
            executorService.submit {
                start.await()
                try {
                    var page = offset
                    while (true) {
                        val response = service.getHouses(page, PAGE_SIZE).execute()
                        val pageHouses = response.body() as List<HouseRes>
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
        executorService.shutdown()

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

        val houseExecutorService = Executors.newFixedThreadPool(houseNames.size)
        val start = CountDownLatch(1)
        val finish = CountDownLatch(houseNames.size)

        val executorService = Executors.newFixedThreadPool(THREAD_POOL_SIZE)

        houses.forEach {
            val house = it
            val houseMembers = house.swornMembers
            houseExecutorService.submit {
                start.await()
                try {
                    val ids = mutableListOf<String>()
                    houseMembers.forEach { url ->
                        val id = url.substringAfterLast("/")
                        ids.add(id)
                    }
                    val characters = loadCharacters(executorService, ids)
                    synchronized(resultLock) {
                        result.add(house to characters)
                    }
                } finally {
                    finish.countDown()
                }
            }
        }

        start.countDown()
        finish.await(10, TimeUnit.SECONDS)
        houseExecutorService.shutdown()

        executorService.shutdown()

        return result;
    }

    private fun loadCharacters(executorService: ExecutorService, ids : List<String>) : List<CharacterRes> {
        val result = ArrayList<CharacterRes>()

        val start = CountDownLatch(1)
        val finish = CountDownLatch(ids.size)

        val lock = Any()
        ids.forEach {
            executorService.submit {
                start.await()
                try {
                    val response = service.getCharacter(it).execute()
                    val character = response.body() as CharacterRes
                    synchronized(lock) {
                        result.add(character)
                    }
                } finally {
                    finish.countDown()
                }
            }
        }

        start.countDown()
        finish.await(10, TimeUnit.SECONDS)

        return result
    }

}

