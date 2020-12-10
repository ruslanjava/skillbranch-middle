package ru.skillbranch.skillarticles.di.modules

import dagger.Module
import dagger.Provides
import ru.skillbranch.skillarticles.di.scopes.ActivityScope
import ru.skillbranch.skillarticles.example.TestActivity

@Module
class ActivityModule(val activity: TestActivity) {

    @ActivityScope
    @Provides
    fun providePair(): Pair<String, String> = "inject" to "pair"

    @Provides
    fun provideTestActivity(): TestActivity = activity

}