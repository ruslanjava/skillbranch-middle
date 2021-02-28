package ru.skillbranch.skillarticles.di.components

import dagger.BindsInstance
import dagger.Subcomponent
import ru.skillbranch.skillarticles.di.modules.ActivityModule
import ru.skillbranch.skillarticles.di.modules.FragmentAModule
import ru.skillbranch.skillarticles.di.modules.FragmentBModule
import ru.skillbranch.skillarticles.di.scopes.ActivityScope
import ru.skillbranch.skillarticles.example.TestActivity

@ActivityScope
@Subcomponent(modules = [ActivityModule::class])
interface ActivityComponent {

    @Subcomponent.Factory
    interface Factory {
        fun create(@BindsInstance activity: TestActivity): ActivityComponent
    }

    fun inject(activity: TestActivity)

    fun plusFragmentAComponent(module: FragmentAModule): FragmentAComponent
    fun plusFragmentBComponent(module: FragmentBModule): FragmentBComponent

}