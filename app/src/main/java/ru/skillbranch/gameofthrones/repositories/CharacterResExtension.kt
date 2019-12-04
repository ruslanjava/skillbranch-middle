package ru.skillbranch.gameofthrones.repositories

import ru.skillbranch.gameofthrones.data.remote.res.CharacterRes

internal fun CharacterRes.toCharacter() : ru.skillbranch.gameofthrones.data.local.entities.Character {
    val id = this.url.substringAfterLast('/')

    val father = this.father.substringAfterLast('/')
    val mother = this.mother.substringAfterLast('/')
    val spouse = this.spouse

    return ru.skillbranch.gameofthrones.data.local.entities.Character(
        id, name, gender, culture,
        born, died,
        titles, aliases,
        father, mother, spouse,
        houseId!!
    )

}