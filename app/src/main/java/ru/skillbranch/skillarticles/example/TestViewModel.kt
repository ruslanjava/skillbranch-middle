package ru.skillbranch.skillarticles.example

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import ru.skillbranch.skillarticles.data.repositories.RootRepository

class TestViewModel
@ViewModelInject constructor(
    rootRepository: RootRepository,
    @Assisted val handler: SavedStateHandle
): ViewModel() {

}