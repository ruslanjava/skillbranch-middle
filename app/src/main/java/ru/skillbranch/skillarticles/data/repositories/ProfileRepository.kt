package ru.skillbranch.skillarticles.data.repositories

import androidx.lifecycle.LiveData
import okhttp3.MultipartBody
import ru.skillbranch.skillarticles.data.local.PrefManager
import ru.skillbranch.skillarticles.data.models.User
import ru.skillbranch.skillarticles.data.remote.NetworkManager

interface IProfileRepository {
    fun getProfile(): LiveData<User?>
    suspend fun uploadAvatar(body: MultipartBody.Part)
    suspend fun removeAvatar()
}

object ProfileRepository: IProfileRepository {

    private val prefs = PrefManager
    private val network = NetworkManager.api

    override fun getProfile(): LiveData<User?> {
        return prefs.profileLive
    }

    override suspend fun uploadAvatar(body: MultipartBody.Part) {
        val (url) = network.upload(body, prefs.accessToken)
        prefs.replaceAvatarUrl(url)
    }

    override suspend fun removeAvatar() {
        val (url) = network.avatarRemove(prefs.accessToken)
        prefs.replaceAvatarUrl(url)
    }

}