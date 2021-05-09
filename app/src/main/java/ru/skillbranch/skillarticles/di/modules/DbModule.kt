package ru.skillbranch.skillarticles.di.modules

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import ru.skillbranch.skillarticles.data.local.AppDb
import ru.skillbranch.skillarticles.data.local.dao.*

@InstallIn(ApplicationComponent::class)
@Module
object DbModule {

    @Provides
    fun provideAppDb(@ApplicationContext context: Context): AppDb = Room.databaseBuilder(
        context,
        AppDb::class.java,
        AppDb.DATABASE_NAME
    ).build()

    @Provides
    fun provideArticlesDao(db: AppDb): ArticlesDao = db.articlesDao()

    @Provides
    fun provideArticleCountsDao(db: AppDb): ArticleCountsDao = db.articleCountsDao()

    @Provides
    fun provideCategoriesDao(db: AppDb): CategoriesDao = db.categoriesDao()

    @Provides
    fun provideArticlePersonalInfoDao(db: AppDb): ArticlePersonalInfosDao = db.articlePersonalInfosDao()

    @Provides
    fun provideTagsDao(db: AppDb): TagsDao = db.tagsDao()

    @Provides
    fun provideArticleContentsDao(db: AppDb): ArticleContentsDao = db.articleContentsDao()

}