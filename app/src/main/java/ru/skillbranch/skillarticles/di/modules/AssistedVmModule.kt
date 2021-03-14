package ru.skillbranch.skillarticles.di.modules

import com.squareup.inject.assisted.dagger2.AssistedModule
import dagger.Module

@AssistedModule
@Module(includes = [AssistedInject_AssistedVmModule::class])
abstract class AssistedVmModule {

}