package ru.skillbranch.gameofthrones.http

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
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

    private val scope = CoroutineScope(Dispatchers.IO)

    @Test
    fun testGetHousesFirstPage() {
        scope.launch {
            val service = IceAndFireServiceFactory.instance
            val houses = service.houses(1, 10)
            assertThat(houses.size, `is`(10))
        }
    }

    @Test
    fun testGetHousesPreLastPage() {
        scope.launch {
            val service = IceAndFireServiceFactory.instance
            val houses = service.houses(50, 10)
            assertThat(houses.size, `is`(0))
        }
    }

}
