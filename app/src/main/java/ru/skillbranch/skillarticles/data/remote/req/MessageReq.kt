package ru.skillbranch.skillarticles.data.remote.req

import ru.skillbranch.skillarticles.data.models.User

data class MessageReq(
    val comment: String,
    val answerToSlug: String?,
    val user: User
)