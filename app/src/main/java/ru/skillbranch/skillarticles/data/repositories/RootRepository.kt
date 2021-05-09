package ru.skillbranch.skillarticles.data.repositories

import androidx.lifecycle.LiveData
import ru.skillbranch.skillarticles.data.local.PrefManager
import ru.skillbranch.skillarticles.data.remote.RestService
import ru.skillbranch.skillarticles.data.remote.req.LoginReq
import ru.skillbranch.skillarticles.data.remote.req.RegisterReq
import ru.skillbranch.skillarticles.data.remote.res.AuthRes
import javax.inject.Inject

interface IRootRepository: IRepository {

    fun isAuth(): LiveData<Boolean>

    suspend fun login(login: String, pass: String)

    suspend fun register(name: String, email: String, pass: String)

}

class RootRepository
@Inject constructor(
        private val preferences: PrefManager,
        private val network: RestService
): IRootRepository {

    override fun isAuth(): LiveData<Boolean> = preferences.isAuthLive

    override suspend fun login(login: String, pass: String) {
        val auth: AuthRes = network.login(LoginReq(login, pass))
        preferences.profile = auth.user
        preferences.accessToken = "Bearer ${auth.accessToken}"
        preferences.refreshToken = auth.refreshToken
    }

    override suspend fun register(name: String, email: String, pass: String) {
        val auth: AuthRes = network.register(RegisterReq(name, email, pass))
        preferences.profile = auth.user
        preferences.accessToken = "Bearer ${auth.accessToken}"
        preferences.refreshToken = auth.refreshToken
    }

}
