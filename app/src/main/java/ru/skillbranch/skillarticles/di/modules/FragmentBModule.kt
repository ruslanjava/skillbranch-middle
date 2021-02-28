package ru.skillbranch.skillarticles.di.modules

import dagger.Module
import dagger.Provides
import ru.skillbranch.skillarticles.data.local.PrefManager
import ru.skillbranch.skillarticles.di.scopes.FragmentScope

@Module
object FragmentBModule {

    @Provides
    @FragmentScope
    fun provideString(prefs: PrefManager): String = "is big text: ${prefs.isBigText}"

}