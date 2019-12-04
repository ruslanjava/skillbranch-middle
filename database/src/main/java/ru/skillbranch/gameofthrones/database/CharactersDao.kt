package ru.skillbranch.gameofthrones.database

import androidx.room.*
import ru.skillbranch.gameofthrones.data.local.entities.*

@Dao
interface CharactersDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(character: Character)

    @Query("SELECT c.id as id, :houseName as house, c.name as name, c.titles as titles, c.aliases as aliases FROM character c WHERE c.house_id = :houseName")
    fun findCharactersByHouseName(houseName: String): List<CharacterItem>

    @Query("SELECT * FROM character WHERE character.id == :id")
    fun findCharacterById(id: String): Character?

    @Query("SELECT * FROM house WHERE house.id == :id")
    fun findHouse(id: String): House

    @Query("SELECT c.id as id, c.name as name, c.house_id as house FROM character c WHERE c.id == :id")
    fun findRelativeCharacter(id: String): RelativeCharacter?

    @Query("SELECT * FROM character LIMIT 1")
    fun getFirstCharacter(): Character?

    @Query("DELETE FROM character")
    fun clearTable()

    @Transaction
    fun insert(characters : List<Character>) {
        for (character in characters) {
            insert(character)
        }
    }

    @Transaction
    fun findCharacterFullById(id: String) : CharacterFull? {
        val character = findCharacterById(id) ?: return null
        val house = findHouse(character.houseId)
        val mother = findRelativeCharacter(character.mother)
        val father = findRelativeCharacter(character.father)
        return character.toCharacterFull(house, mother, father)
    }

}