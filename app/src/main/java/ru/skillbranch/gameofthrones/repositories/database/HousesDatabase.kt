package ru.skillbranch.gameofthrones.repositories.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import ru.skillbranch.gameofthrones.data.local.entities.House
import ru.skillbranch.gameofthrones.data.local.entities.Character
import ru.skillbranch.gameofthrones.data.local.entities.StringListConverter

@Database(entities = [House::class, Character::class], version = 1)
@TypeConverters(StringListConverter::class)
abstract class HousesDatabase : RoomDatabase() {

    abstract fun housesDao(): HousesDao
    abstract fun charactersDao(): CharactersDao

    fun dropDatabase() {
        housesDao().clearTable()
        charactersDao().clearTable()
    }

    companion object {

        private var INSTANCE: HousesDatabase? = null

        fun getInstance(context: Context): HousesDatabase {
            if (INSTANCE == null) {
                synchronized(HousesDatabase::class) {
                    INSTANCE = Room.databaseBuilder(
                        context.applicationContext, HousesDatabase::class.java, "houses.db"
                    ).build()
                }
            }
            return INSTANCE!!
        }

        fun destroyInstance() {
            INSTANCE = null
        }

    }

}