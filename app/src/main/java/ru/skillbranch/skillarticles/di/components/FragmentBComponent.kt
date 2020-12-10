package ru.skillbranch.skillarticles.di.components

import dagger.Component
import ru.skillbranch.skillarticles.di.modules.FragmentBModule
import ru.skillbranch.skillarticles.di.scopes.FragmentScope
import ru.skillbranch.skillarticles.example.FragmentB

@FragmentScope
@Component(dependencies = [ActivityComponent::class], modules = [FragmentBModule::class])
interface FragmentBComponent {

    fun inject(fragment: FragmentB)

}