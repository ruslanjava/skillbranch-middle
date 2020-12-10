package ru.skillbranch.skillarticles.data.remote.interceptors

import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import ru.skillbranch.skillarticles.App
import ru.skillbranch.skillarticles.data.remote.NetworkManager
import ru.skillbranch.skillarticles.data.remote.req.RefreshReq
import java.net.HttpURLConnection

class TokenAuthenticator() : Authenticator {

    val prefs by lazy {
        App.appComponent.getPreferences()
    }

    override fun authenticate(route: Route?, response: Response): Request? {
        if (response.code != HttpURLConnection.HTTP_UNAUTHORIZED) {
            return null
        }

        val res = NetworkManager.api.refreshAccessToken(
            RefreshReq(prefs.refreshToken)
        ).execute()

        if (!res.isSuccessful) {
            return null
        }

        val refreshRes = res.body()!!

        prefs.accessToken = "Bearer ${refreshRes.accessToken}"
        prefs.refreshToken = refreshRes.refreshToken

        return response.request.newBuilder()
            .header("Authorization", prefs.accessToken)
            .build()
    }

}
