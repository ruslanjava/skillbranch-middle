package ru.skillbranch.skillarticles.di.modules

import androidx.fragment.app.Fragment
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.FragmentComponent
import ru.skillbranch.skillarticles.data.repositories.ArticlesRepository
import ru.skillbranch.skillarticles.data.repositories.IRepository
import ru.skillbranch.skillarticles.ui.article.ArticleFragment
import ru.skillbranch.skillarticles.ui.article.IArticleView

@InstallIn(FragmentComponent::class)
@Module
abstract class ArticleModule {

    @Binds
    abstract fun bindArticleRepository(repo: ArticlesRepository): IRepository

    @Binds
    abstract fun bindArticleView(fragment: ArticleFragment): IArticleView

    companion object {

        @Provides
        fun provideArticleFragment(fragment: Fragment): ArticleFragment =
            fragment as ArticleFragment

    }

}