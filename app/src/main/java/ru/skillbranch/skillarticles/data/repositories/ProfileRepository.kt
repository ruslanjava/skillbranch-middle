package ru.skillbranch.skillarticles.data.repositories

import androidx.lifecycle.LiveData
import okhttp3.MultipartBody
import ru.skillbranch.skillarticles.data.local.PrefManager
import ru.skillbranch.skillarticles.data.models.User
import ru.skillbranch.skillarticles.data.remote.RestService
import ru.skillbranch.skillarticles.data.remote.req.EditProfileReq
import javax.inject.Inject

interface IProfileRepository {
    fun getProfile(): LiveData<User?>
    suspend fun uploadAvatar(body: MultipartBody.Part)
    suspend fun removeAvatar()
    suspend fun editProfile(name: String, about: String)
}

class ProfileRepository @Inject constructor(
    val prefs: PrefManager, val network: RestService
) : IProfileRepository {

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

    override suspend fun editProfile(name: String, about: String) {
        val profileRes = network.editProfile(EditProfileReq(name, about), prefs.accessToken)
        prefs.profile = prefs.profile!!.copy(
            id = profileRes.id,
            name = profileRes.name,
            avatar = profileRes.avatar,
            rating = profileRes.rating,
            respect = profileRes.respect,
            about = profileRes.about
        )
    }
}
