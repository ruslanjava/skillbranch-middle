package ru.skillbranch.gameofthrones.repositories

import ru.skillbranch.gameofthrones.HouseName
import ru.skillbranch.gameofthrones.data.local.entities.House
import ru.skillbranch.gameofthrones.data.remote.res.HouseRes

internal fun HouseRes.toHouse() : House {
    val id = shortName(this.name)

    val currentLord = this.currentLord.substringAfterLast('/')
    val heir = this.heir.substringAfterLast('/')

    val weapons = this.ancestralWeapons

    return House(
        id, name, region, coatOfArms, words,
        titles, seats,
        currentLord, heir,
        overlord, founded,
        founder, diedOut,
        weapons
    )
}

private fun shortName(name: String) : String {
    for (houseName in HouseName.values()) {
        if (houseName.fullName == name) {
            return houseName.shortName
        }
    }
    return name
}