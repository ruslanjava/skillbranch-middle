package ru.skillbranch.skillarticles.di.modules

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.BindsInstance
import dagger.Module
import dagger.Provides
import ru.skillbranch.skillarticles.data.local.PrefManager
import ru.skillbranch.skillarticles.data.remote.RestService
import ru.skillbranch.skillarticles.data.repositories.IRepository
import ru.skillbranch.skillarticles.data.repositories.RootRepository
import ru.skillbranch.skillarticles.di.scopes.ActivityScope
import ru.skillbranch.skillarticles.example.TestActivity
import ru.skillbranch.skillarticles.example.TestViewModel
import java.util.prefs.Preferences

@Module
abstract class ActivityModule {

    @Binds
    abstract fun bindRootRepository(repository: RootRepository): IRepository

    @Binds
    abstract fun bindViewModel(viewModel: TestViewModel): ViewModel

}