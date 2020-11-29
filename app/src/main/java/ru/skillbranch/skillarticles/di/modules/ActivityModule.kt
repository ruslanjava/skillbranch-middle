package ru.skillbranch.skillarticles.di.modules

import dagger.Module
import dagger.Provides

@Module
class ActivityModule {

    @Provides
    fun providePair(): Pair<String, String> = "inject" to "pair"

}