package ru.skillbranch.gameofthrones.repositories

import androidx.annotation.VisibleForTesting
import kotlinx.coroutines.*
import ru.skillbranch.gameofthrones.HouseName
import ru.skillbranch.gameofthrones.app.application.GameOfThronesApplication
import ru.skillbranch.gameofthrones.data.local.entities.CharacterFull
import ru.skillbranch.gameofthrones.data.local.entities.CharacterItem
import ru.skillbranch.gameofthrones.data.local.entities.House
import ru.skillbranch.gameofthrones.data.remote.res.CharacterRes
import ru.skillbranch.gameofthrones.data.remote.res.HouseRes
import ru.skillbranch.gameofthrones.database.HousesDatabase
import ru.skillbranch.gameofthrones.http.IceAndFireClient

object RootRepository {

    private val job = Job()
    private val ioScope = CoroutineScope(Dispatchers.IO + job)

    /**
     * Получение данных о всех домах
     * @param result - колбек содержащий в себе список данных о домах
     */
    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    fun getAllHouses(result : (houses : List<HouseRes>) -> Unit) {
        ioScope.launch {
            val houses = IceAndFireClient.getAllHouses()
            result.invoke(houses)
        }
    }

    /**
     * Получение данных о требуемых домах по их полным именам (например фильтрация всех домов)
     * @param houseNames - массив полных названий домов (смотри AppConfig)
     * @param result - колбек содержащий в себе список данных о домах
     */
    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    fun getNeedHouses(vararg houseNames: String, result : (houses : List<HouseRes>) -> Unit) {
        ioScope.launch {
            val neededHouses = IceAndFireClient.getNeededHouses(houseNames)
            result.invoke(neededHouses)
        }
    }

    /**
     * Получение данных о требуемых домах по их полным именам и персонажах в каждом из домов
     * @param houseNames - массив полных названий домов (смотри AppConfig)
     * @param result - колбек содержащий в себе список данных о доме и персонажей в нем (Дом - Список Персонажей в нем)
     */
    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    fun getNeedHouseWithCharacters(vararg houseNames: String, result : (houses : List<Pair<HouseRes, List<CharacterRes>>>) -> Unit) {
        ioScope.launch {
            val pairs = IceAndFireClient.getNeedHousesWithCharacters(houseNames)
            for (pair in pairs) {
                val house = pair.first
                val houseId = shortHouseName(house.name)
                val characters = pair.second
                for (character in characters) {
                    character.houseId = houseId
                }
            }
            result.invoke(pairs)
        }
    }

    /**
     * Запись данных о домах в DB
     * @param houses - Список персонажей (модель HouseRes - модель ответа из сети)
     * необходимо произвести трансформацию данных
     * @param complete - колбек о завершении вставки записей db
     */
    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    fun insertHouses(houses : List<HouseRes>, complete: () -> Unit) {
        ioScope.launch {
            val dbHouses = mutableListOf<House>()
            for (houseRes in houses) {
                dbHouses.add(houseRes.toHouse())
            }
            val database = HousesDatabase.getInstance(GameOfThronesApplication.context)
            database?.housesDao()?.insert(dbHouses)
            complete.invoke()
        }
    }

    /**
     * Запись данных о персонажах в DB
     * @param characters - Список персонажей (модель CharterRes - модель ответа из сети)
     * необходимо произвести трансформацию данных
     * @param complete - колбек о завершении вставки записей db
     */
    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    fun insertCharacters(characters : List<CharacterRes>, complete: () -> Unit) {
        ioScope.launch {
            val dbCharacters =
                characters.map { characterRes -> characterRes.toCharacter() }.toList()
            val database = HousesDatabase.getInstance(GameOfThronesApplication.context)
            database?.charactersDao()?.insert(dbCharacters)
            complete.invoke()
        }
    }

    /**
     * При вызове данного метода необходимо выполнить удаление всех записей в db
     * @param complete - колбек о завершении очистки db
     */
    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    fun dropDb(complete: () -> Unit) {
        ioScope.launch {
            val database = HousesDatabase.getInstance(GameOfThronesApplication.context)
            database?.dropDatabase()
            complete.invoke()
        }
    }

    /**
     * Поиск всех персонажей по имени дома, должен вернуть список краткой информации о персонажах
     * дома - смотри модель CharacterItem
     * @param name - краткое имя дома (его первычный ключ)
     * @param result - колбек содержащий в себе список краткой информации о персонажах дома
     */
    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    fun findCharactersByHouseName(name : String, result: (charters : List<CharacterItem>) -> Unit) {
        ioScope.launch {
            val database = HousesDatabase.getInstance(GameOfThronesApplication.context)
            val characters = database?.charactersDao()?.findCharactersByHouseName(name)
            result.invoke(characters!!)
        }
    }

    /**
     * Поиск персонажа по его идентификатору, должен вернуть полную информацию о персонаже
     * и его родственных отношения - смотри модель CharacterFull
     * @param id - идентификатор персонажа
     * @param result - колбек содержащий в себе полную информацию о персонаже
     */
    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    fun findCharacterFullById(id : String, result: (charter : CharacterFull) -> Unit) {
        ioScope.launch {
            val database = HousesDatabase.getInstance(GameOfThronesApplication.context)
            val character = database?.charactersDao()?.findCharacterFullById(id)
            if (character != null) {
                result.invoke(character)
            }
        }
    }

    /**
     * Метод возвращет true если в базе нет ни одной записи, иначе false
     * @param result - колбек о завершении очистки db
     */
    fun isNeedUpdate(result: (isNeed : Boolean) -> Unit) {
        ioScope.launch {
            val database = HousesDatabase.getInstance(GameOfThronesApplication.context)
            val house = database?.housesDao()?.getFirstHouse()

            // если в базе есть хотя бы 1 дом, значит, база уже непустая -> возвращаем false
            if (house != null) {
                result.invoke(false)
            } else {
                // если в базе нет ни одного персонажа, возвращаем true
                val character = database?.charactersDao()?.getFirstCharacter()
                result.invoke(character == null)
            }
        }
    }

    private fun shortHouseName(name: String) : String {
        for (houseName in HouseName.values()) {
            if (houseName.fullName == name) {
                return houseName.shortName
            }
        }
        return name
    }

}