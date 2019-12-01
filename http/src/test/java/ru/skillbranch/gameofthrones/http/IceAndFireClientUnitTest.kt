package ru.skillbranch.gameofthrones.http

import org.hamcrest.MatcherAssert.assertThat
import org.junit.Test
import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.lessThan
import ru.skillbranch.gameofthrones.AppConfig
import ru.skillbranch.gameofthrones.data.remote.res.CharacterRes
import ru.skillbranch.gameofthrones.data.remote.res.HouseRes

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class IceAndFireClientUnitTest {

    @Test
    fun testGetAllHouses() {
        val startTime = System.currentTimeMillis()
        val houses = IceAndFireClient.getAllHouses()
        val endTime = System.currentTimeMillis()

        val timePassed = (endTime - startTime)
        println("Time passed: $timePassed")
        assertThat(timePassed, lessThan(5000L))
        assertThat(houses.size, `is`(444))
    }

    @Test
    fun testGetNeedHousesWithCharacters() {
        val startTime = System.currentTimeMillis()
        val pairs = IceAndFireClient.getNeedHousesWithCharacters(AppConfig.NEED_HOUSES)
        val endTime = System.currentTimeMillis()

        val timePassed = (endTime - startTime)
        println("Time passed: $timePassed")
        assertThat(timePassed, lessThan(10000L))

        val starkMembers = getCharacters(pairs, "House Stark of Winterfell")
        assertThat(starkMembers.size, `is`(88))
    }

    private fun getCharacters(pairs: List<Pair<HouseRes, List<CharacterRes>>>, houseName: String) : List<CharacterRes> {
        pairs.forEach {
            if (it.first.name.equals(houseName)) {
                return it.second
            }
        }
        return emptyList()
    }

}
