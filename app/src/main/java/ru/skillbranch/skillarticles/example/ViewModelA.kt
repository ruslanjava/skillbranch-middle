package ru.skillbranch.skillarticles.example

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import ru.skillbranch.skillarticles.data.repositories.RootRepository

class ViewModelA @ViewModelInject constructor(
    val repository: RootRepository,
    @Assisted val handle: SavedStateHandle
): ViewModel() {

}