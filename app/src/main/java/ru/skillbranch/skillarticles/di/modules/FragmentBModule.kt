package ru.skillbranch.skillarticles.di.modules

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.FragmentComponent
import ru.skillbranch.skillarticles.data.local.PrefManager
import ru.skillbranch.skillarticles.di.scopes.FragmentScope

@InstallIn(FragmentComponent::class)
@Module
object FragmentBModule {

    @Provides
    @FragmentScope
    fun provideString(prefs: PrefManager): String = "is big text: ${prefs.isBigText}"

}