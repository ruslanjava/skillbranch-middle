package ru.skillbranch.gameofthrones.data.local.entities

import androidx.room.*

@Entity(
    tableName = "character",
    indices = [
        Index(value = ["house_id"]),
        Index(value = ["father"]),
        Index(value = ["mother"])
    ]
)
data class Character(

    @PrimaryKey
    val id: String,

    val name: String,
    val gender: String,
    val culture: String,
    val born: String,
    val died: String,

    val titles: List<String> = listOf(),
    val aliases: List<String> = listOf(),

    val father: String, //rel
    val mother: String, //rel

    val spouse: String,

    @ColumnInfo(name = "house_id")
    val houseId: String //rel
) {

}

data class CharacterItem(
    val id: String,

    val house: String, //rel
    val name: String,
    val titles: List<String>,
    val aliases: List<String>
)

data class CharacterFull(
    val id: String,
    val name: String,
    val words: String,
    val born: String,
    val died: String,

    @TypeConverters(StringListConverter::class)
    val titles: List<String>,

    @TypeConverters(StringListConverter::class)
    val aliases: List<String>,

    val house:String, //rel

    val father: RelativeCharacter?,
    val mother: RelativeCharacter?
)

data class RelativeCharacter(
    val id: String,
    val name: String,
    val house:String //rel
)