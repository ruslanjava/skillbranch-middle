package ru.skillbranch.gameofthrones.repositories

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.*
import ru.skillbranch.gameofthrones.HouseName
import ru.skillbranch.gameofthrones.App
import ru.skillbranch.gameofthrones.AppConfig
import ru.skillbranch.gameofthrones.data.local.entities.CharacterFull
import ru.skillbranch.gameofthrones.data.local.entities.CharacterItem
import ru.skillbranch.gameofthrones.data.local.entities.House
import ru.skillbranch.gameofthrones.data.remote.res.CharacterRes
import ru.skillbranch.gameofthrones.data.remote.res.HouseRes
import ru.skillbranch.gameofthrones.repositories.database.CharactersDao
import ru.skillbranch.gameofthrones.repositories.database.HousesDao
import ru.skillbranch.gameofthrones.repositories.database.HousesDatabase
import ru.skillbranch.gameofthrones.repositories.http.IceAndFireClient
import ru.skillbranch.gameofthrones.repositories.http.IceAndFireServiceFactory
import kotlin.coroutines.CoroutineContext

object RootRepository {

    private val characters: MutableMap<String, LiveData<List<CharacterItem>>> = mutableMapOf()

    private val api = IceAndFireServiceFactory.instance
    private var housesDao: HousesDao
    private var charactersDao: CharactersDao

    private val errHandler = CoroutineExceptionHandler { _, exception ->
        println("Caught $exception")
        exception.printStackTrace()
    }

    init {
        val housesDatabase = HousesDatabase.getInstance(App.context)
        housesDao = housesDatabase.housesDao()
        charactersDao = housesDatabase.charactersDao()
    }

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO + errHandler)

    /**
     * Получение данных о всех домах
     * @param result - колбек содержащий в себе список данных о домах
     */
    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    fun getAllHouses(result : (houses : List<HouseRes>) -> Unit) {
        val houses = IceAndFireClient.getAllHouses()
        result.invoke(houses)
    }

    /**
     * Получение данных о требуемых домах по их полным именам (например фильтрация всех домов)
     * @param houseNames - массив полных названий домов (смотри AppConfig)
     * @param result - колбек содержащий в себе список данных о домах
     */
    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    fun getNeedHouses(vararg houseNames: String, result : (houses : List<HouseRes>) -> Unit) {
        val neededHouses = getNeedHouses(*houseNames)
        result.invoke(neededHouses)
    }

    private fun getNeedHouses(vararg houseNames: String) : List<HouseRes> {
        return IceAndFireClient.getNeededHouses(houseNames)
    }

    /**
     * Получение данных о требуемых домах по их полным именам и персонажах в каждом из домов
     * @param houseNames - массив полных названий домов (смотри AppConfig)
     * @param result - колбек содержащий в себе список данных о доме и персонажей в нем (Дом - Список Персонажей в нем)
     */
    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    fun getNeedHouseWithCharacters(vararg houseNames: String, result : (houses : List<Pair<HouseRes, List<CharacterRes>>>) -> Unit) {
        val pairs = getNeedHouseWithCharacters(*houseNames)
        result.invoke(pairs)
    }

    fun getNeedHouseWithCharacters(vararg houseNames: String) : List<Pair<HouseRes, List<CharacterRes>>> {
        val pairs = IceAndFireClient.getNeedHousesWithCharacters(houseNames)
        for (pair in pairs) {
            val house = pair.first
            val houseId = shortHouseName(house.name)
            val characters = pair.second
            for (character in characters) {
                character.houseId = houseId
            }
        }
        return pairs
    }

    suspend fun needHouseWithCharacters(vararg houseNames: String) : List<Pair<HouseRes, List<CharacterRes>>> {
        val result = mutableListOf<Pair<HouseRes, List<CharacterRes>>>()
        val houses: List<HouseRes> = getNeedHouses(*houseNames)

        scope.launch {
            houses.forEach { house ->
                var i = 0
                // println("houseByName" ${houseByName.url} scope this ctx ${this.coroutineContext}")
                val characters = mutableListOf<CharacterRes>()
                result.add(house to characters)
                house.members.forEach { character ->
                    launch(CoroutineName("character $character")) {
                        api.character(character)
                            .apply { houseId = house.shortName }
                            .also { characters.add(it) }
                        i++
                        println("complete coroutine $i/${house.swornMembers.size} ${house.name} ${this.coroutineContext[CoroutineName]}")
                    }
                }

            }
        }.join()
        return result
    }

    /**
     * Запись данных о домах в DB
     * @param houses - Список персонажей (модель HouseRes - модель ответа из сети)
     * необходимо произвести трансформацию данных
     * @param complete - колбек о завершении вставки записей db
     */
    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    fun insertHouses(houses : List<HouseRes>, complete: () -> Unit) {
        val dbHouses = mutableListOf<House>()
        for (houseRes in houses) {
            dbHouses.add(houseRes.toHouse())
        }
        housesDao.insert(dbHouses)
        complete.invoke()
    }

    /**
     * Запись данных о персонажах в DB
     * @param characters - Список персонажей (модель CharterRes - модель ответа из сети)
     * необходимо произвести трансформацию данных
     * @param complete - колбек о завершении вставки записей db
     */
    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    fun insertCharacters(characters : List<CharacterRes>, complete: () -> Unit) {
        val dbCharacters = characters.map { characterRes -> characterRes.toCharacter() }.toList()
        charactersDao.insert(dbCharacters)
        complete.invoke()
    }

    /**
     * При вызове данного метода необходимо выполнить удаление всех записей в db
     * @param complete - колбек о завершении очистки db
     */
    fun dropDb(complete: () -> Unit) {
        HousesDatabase.getInstance(App.context).dropDatabase()
        complete.invoke()
    }

    /**
     * Поиск всех персонажей по имени дома, должен вернуть список краткой информации о персонажах
     * дома - смотри модель CharacterItem
     * @param name - краткое имя дома (его первычный ключ)
     * @param result - колбек содержащий в себе список краткой информации о персонажах дома
     */
    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    fun findCharactersByHouseName(name : String, result: (charters : List<CharacterItem>) -> Unit) {
        val characters = charactersDao.findCharactersByHouseName(name)
        result.invoke(characters)
    }

    /**
     * Поиск персонажа по его идентификатору, должен вернуть полную информацию о персонаже
     * и его родственных отношения - смотри модель CharacterFull
     * @param id - идентификатор персонажа
     * @param result - колбек содержащий в себе полную информацию о персонаже
     */
    fun findCharacterFullById(id : String, result: (charter : CharacterFull) -> Unit) {
        val character = charactersDao.findCharacterFullById(id)
        if (character != null) {
            result.invoke(character)
        }
    }

    suspend fun sync() {
        val pairs = getNeedHouseWithCharacters(*AppConfig.NEED_HOUSES)
        val initial = mutableListOf<House>() to mutableListOf<ru.skillbranch.gameofthrones.data.local.entities.Character>()

        val lists = pairs.fold(initial) { acc, (houseRes, charactersList) ->
            val house:House = houseRes.toHouse()
            val characters = charactersList.map { it.toCharacter() }
            acc.also { (hs, ch) ->
                hs.add(house)
                ch.addAll(characters)
            }
        }

        housesDao.insert(lists.first)
        charactersDao.insert(lists.second)
    }

    /**
     * Метод возвращет true если в базе нет ни одной записи, иначе false
     * @param result - колбек о завершении очистки db
     */
    fun isNeedUpdate(result: (isNeed : Boolean) -> Unit) {
        val isNeedUpdate = isNeedUpdate()
        result.invoke(isNeedUpdate)
    }

    /**
     * Метод возвращет true если в базе нет ни одной записи, иначе false
     * @param result - колбек о завершении очистки db
     */
    fun isNeedUpdate(): Boolean {
        val house = housesDao.getFirstHouse()

        // если в базе есть хотя бы 1 дом, значит, база уже непустая -> возвращаем false
        if (house != null) {
            return false
        }

        // если в базе нет ни одного персонажа, возвращаем true
        val character = charactersDao.getFirstCharacter()
        return character == null
    }

    private fun shortHouseName(name: String) : String {
        for (houseName in HouseName.values()) {
            if (houseName.fullName == name) {
                return houseName.shortName
            }
        }
        return name
    }

    @Synchronized
    fun findCharacters(houseName: String): LiveData<List<CharacterItem>> {
        var result = characters[houseName]
        if (result == null) {
            result = MutableLiveData()
            characters[houseName] = result
        }
        return result
    }

}