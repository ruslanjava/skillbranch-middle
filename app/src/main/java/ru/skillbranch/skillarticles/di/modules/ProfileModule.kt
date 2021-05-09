package ru.skillbranch.skillarticles.di.modules

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.FragmentComponent
import ru.skillbranch.skillarticles.data.repositories.IRepository
import ru.skillbranch.skillarticles.data.repositories.ProfileRepository

@InstallIn(FragmentComponent::class)
@Module
abstract class ProfileModule {

    @Binds
    abstract fun bindProfileRepository(repo: ProfileRepository): IRepository

}