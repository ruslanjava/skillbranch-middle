package ru.skillbranch.gameofthrones.http

import org.hamcrest.MatcherAssert.assertThat
import org.junit.Test
import org.hamcrest.Matchers.`is`
import ru.skillbranch.gameofthrones.data.remote.res.HouseRes
import ru.skillbranch.gameofthrones.repositories.http.IceAndFireServiceFactory

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class IceAndFireApiServiceUnitTest {

    @Test
    fun testGetHousesFirstPage() {
        val service = IceAndFireServiceFactory.newInstance()
        val response = service.getHouses(1, 10).execute()
        val houses = response.body() as List<HouseRes>
        assertThat(houses.size, `is`(10))
    }

    @Test
    fun testGetHousesPreLastPage() {
        val service = IceAndFireServiceFactory.newInstance()
        val response = service.getHouses(50, 10).execute()
        val houses = response.body() as List<HouseRes>
        assertThat(houses.size, `is`(0))
    }

}
