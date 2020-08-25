package ru.skillbranch.skillarticles.extensions.data

import ru.skillbranch.skillarticles.data.local.entities.Author
import ru.skillbranch.skillarticles.data.remote.res.AuthorRes

fun AuthorRes.toAuthor() : Author {
    return Author(id, avatar, name)
}