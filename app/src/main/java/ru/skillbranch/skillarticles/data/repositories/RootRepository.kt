package ru.skillbranch.skillarticles.data.repositories

import androidx.lifecycle.LiveData
import ru.skillbranch.skillarticles.data.local.PrefManager
import ru.skillbranch.skillarticles.data.remote.NetworkManager
import ru.skillbranch.skillarticles.data.remote.RestService
import ru.skillbranch.skillarticles.data.remote.req.LoginReq

object RootRepository {

    val preferences: PrefManager = PrefManager
    private val network: RestService = NetworkManager.api

    fun isAuth() : LiveData<Boolean> = preferences.isAuth()

    fun setAuth(auth:Boolean) = preferences.setAuth(auth)

    suspend fun login(login: String, pass: String) {
        val auth = network.login(LoginReq(login, pass))
        preferences.profile = auth.user
        preferences.accessToken = "Bearer ${auth.accessToken}"
        preferences.refreshToken = auth.refreshToken
    }

}