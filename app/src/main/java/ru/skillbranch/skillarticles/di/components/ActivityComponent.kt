package ru.skillbranch.skillarticles.di.components

import dagger.Component
import ru.skillbranch.skillarticles.data.local.PrefManager
import ru.skillbranch.skillarticles.di.modules.ActivityModule
import ru.skillbranch.skillarticles.di.scopes.ActivityScope
import ru.skillbranch.skillarticles.example.TestActivity

@ActivityScope
@Component(dependencies = [AppComponent::class], modules = [ActivityModule::class])
interface ActivityComponent {

    fun inject(activity: TestActivity)
    fun getPreferences(): PrefManager
    fun getActivity(): TestActivity

}