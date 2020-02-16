package ru.skillbranch.gameofthrones.repositories.database

import androidx.room.*
import androidx.room.OnConflictStrategy.IGNORE
import androidx.room.OnConflictStrategy.REPLACE
import ru.skillbranch.gameofthrones.data.local.entities.Character
import ru.skillbranch.gameofthrones.data.local.entities.House

@Dao
interface HousesDao {

    @Insert(onConflict = IGNORE)
    fun insert(house: House): Long

    @Update
    fun update(house: House)

    @Query("SELECT * FROM house LIMIT 1")
    fun getFirstHouse(): House?

    @Query("DELETE FROM house")
    fun clearTable()

    @Transaction
    fun insert(houses : List<House>) {
        for (house in houses) {
            val id: Long = insert(house)
            if (id == -1L) {
                update(house)
            }
        }
    }

}