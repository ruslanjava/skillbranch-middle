package ru.skillbranch.skillarticles.data.remote.req

data class RegisterReq(
    val name: String,
    val login: String,
    val password: String
)