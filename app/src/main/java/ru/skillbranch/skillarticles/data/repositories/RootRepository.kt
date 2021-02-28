package ru.skillbranch.skillarticles.data.repositories

import androidx.lifecycle.LiveData
import ru.skillbranch.skillarticles.App
import ru.skillbranch.skillarticles.data.remote.RestService
import ru.skillbranch.skillarticles.data.remote.req.LoginReq
import ru.skillbranch.skillarticles.data.remote.req.RegisterReq
import ru.skillbranch.skillarticles.data.remote.res.AuthRes

object RootRepository {

    private val preferences by lazy {
        App.appComponent.getPrefManager()
    }
    private val network: RestService by lazy {
        App.appComponent.getNetworkManager().api
    }

    fun isAuth(): LiveData<Boolean> = preferences.isAuthLive

    suspend fun login(login: String, pass: String) {
        val auth: AuthRes = network.login(LoginReq(login, pass))
        preferences.profile = auth.user
        preferences.accessToken = "Bearer ${auth.accessToken}"
        preferences.refreshToken = auth.refreshToken
    }

    suspend fun register(name: String, email: String, pass: String) {
        val auth: AuthRes = network.register(RegisterReq(name, email, pass))
        preferences.profile = auth.user
        preferences.accessToken = "Bearer ${auth.accessToken}"
        preferences.refreshToken = auth.refreshToken
    }

}
