package ru.skillbranch.skillarticles.di.modules

import android.R
import android.content.Context
import android.widget.SimpleCursorAdapter
import androidx.cursoradapter.widget.CursorAdapter
import androidx.fragment.app.Fragment
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.FragmentComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import ru.skillbranch.skillarticles.data.repositories.ArticlesRepository
import ru.skillbranch.skillarticles.data.repositories.IRepository
import ru.skillbranch.skillarticles.ui.articles.ArticlesFragment
import ru.skillbranch.skillarticles.ui.articles.IArticleClickListener

@InstallIn(FragmentComponent::class)
@Module
abstract class ArticlesModule {

    @Binds
    abstract fun bindArticleRepository(repo: ArticlesRepository): IRepository

    @Binds
    abstract fun bindClickListener(fragment: ArticlesFragment): IArticleClickListener

    companion object {

        @Provides
        fun provideArticlesFragment(fragment: Fragment): ArticlesFragment = fragment as ArticlesFragment

        @Provides
        fun provideSimpleCursorAdapter(fragment: Fragment): SimpleCursorAdapter = SimpleCursorAdapter(
                fragment.context,
                R.layout.simple_list_item_1,
                null, // cursor
                arrayOf("tag"), // cursor column for bind on view
                intArrayOf(R.id.text1), // text view id for bind data from cursor columns
                CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER
            )

    }

}