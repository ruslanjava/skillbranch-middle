package ru.skillbranch.skillarticles.data.remote.req

data class MessageReq(
    val comment: String,
    val answerToSlug: String?
)