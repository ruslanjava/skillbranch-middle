package ru.skillbranch.skillarticles.data.local.entities

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import ru.skillbranch.skillarticles.data.local.dao.BaseDao

@Dao
interface ArticleContentsDao: BaseDao<ArticleContent> {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    override fun insert(obj: ArticleContent): Long

}