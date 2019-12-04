package ru.skillbranch.gameofthrones.database

import androidx.room.*
import androidx.room.OnConflictStrategy.REPLACE
import ru.skillbranch.gameofthrones.data.local.entities.Character
import ru.skillbranch.gameofthrones.data.local.entities.House

@Dao
interface HousesDao {

    @Insert(onConflict = REPLACE)
    fun insert(house: House)

    @Query("SELECT * FROM house LIMIT 1")
    fun getFirstHouse(): House?

    @Query("DELETE FROM house")
    fun clearTable()

    @Transaction
    fun insert(houses : List<House>) {
        for (house in houses) {
            insert(house)
        }
    }

}