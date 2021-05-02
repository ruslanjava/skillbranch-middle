package ru.skillbranch.skillarticles.di.modules

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.FragmentComponent
import ru.skillbranch.skillarticles.di.scopes.FragmentScope
import javax.inject.Named

@InstallIn(FragmentComponent::class)
@Module
object FragmentAModule {

    @Provides
    @FragmentScope
    @Named("dep1")
    fun provideDependency1(): String = "dependency1"

    @Provides
    @FragmentScope
    @Named("dep2")
    fun provideDependency2(): String = "dependency2"

}