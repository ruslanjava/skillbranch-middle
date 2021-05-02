package ru.skillbranch.skillarticles.di.modules

import android.app.Activity
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import ru.skillbranch.skillarticles.example.TestActivity

@InstallIn(ActivityComponent::class)
@Module
class ActivityModule {

    @Provides
    fun provideActivity(activity: Activity): TestActivity = activity as TestActivity

}