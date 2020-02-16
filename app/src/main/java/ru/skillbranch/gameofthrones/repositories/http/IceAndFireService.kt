package ru.skillbranch.gameofthrones.repositories.http

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import ru.skillbranch.gameofthrones.data.remote.res.CharacterRes
import ru.skillbranch.gameofthrones.data.remote.res.HouseRes

interface IceAndFireService {

    @GET("houses")
    suspend fun houses(@Query("page") page : Int, @Query("pageSize") pageSize: Int): List<HouseRes>

    @GET("characters/{id}")
    suspend fun character(@Path("id") id : String): CharacterRes

    @GET("houses")
    suspend fun houseByName(@Query("name") name: String) : List<HouseRes>

}