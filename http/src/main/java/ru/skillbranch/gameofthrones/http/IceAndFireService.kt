package ru.skillbranch.gameofthrones.http

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import ru.skillbranch.gameofthrones.data.remote.res.CharacterRes
import ru.skillbranch.gameofthrones.data.remote.res.HouseRes

interface IceAndFireService {

    @GET("houses")
    fun getHouses(@Query("page") page : Int, @Query("pageSize") pageSize: Int): Call<List<HouseRes>>

    @GET("characters/{id}")
    fun getCharacter(@Path("id") id : String): Call<CharacterRes>

}