package ru.skillbranch.skillarticles.di.components

import dagger.Component
import ru.skillbranch.skillarticles.di.modules.ActivityModule
import ru.skillbranch.skillarticles.example.TestActivity

@Component(modules = [ActivityModule::class])
interface ActivityComponent {

    fun inject(activity: TestActivity)

}