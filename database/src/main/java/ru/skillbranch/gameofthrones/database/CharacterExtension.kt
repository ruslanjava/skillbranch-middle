package ru.skillbranch.gameofthrones.database

import ru.skillbranch.gameofthrones.data.local.entities.Character
import ru.skillbranch.gameofthrones.data.local.entities.CharacterFull
import ru.skillbranch.gameofthrones.data.local.entities.House
import ru.skillbranch.gameofthrones.data.local.entities.RelativeCharacter

fun Character.toCharacterFull(house: House, mother: RelativeCharacter?, father: RelativeCharacter?) : CharacterFull {
    return CharacterFull(id, name, house.words, born, died, titles, aliases, house.name, father, mother)
}