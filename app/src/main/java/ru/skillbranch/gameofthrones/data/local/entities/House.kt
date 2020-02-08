package ru.skillbranch.gameofthrones.data.local.entities

import androidx.room.*

@Entity(
    tableName = "house",
    indices = [
        Index(value = ["name"]),
        Index(value = ["current_lord"]),
        Index(value = ["heir"]),
        Index(value = ["founder"])
    ]
)
data class House(

    @PrimaryKey
    val id: String,

    val name: String,
    val region: String,
    val coatOfArms: String,
    val words: String,

    val titles: List<String>,
    val seats: List<String>,

    @ColumnInfo(name = "current_lord")
    val currentLord: String, //rel
    val heir: String, //rel

    val overlord: String,
    val founded: String,

    val founder: String, //rel

    val diedOut: String,
    val ancestralWeapons: List<String>
)