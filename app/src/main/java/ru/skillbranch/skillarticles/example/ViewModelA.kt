package ru.skillbranch.skillarticles.example

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.squareup.inject.assisted.Assisted
import com.squareup.inject.assisted.AssistedInject
import ru.skillbranch.skillarticles.data.repositories.RootRepository
import javax.inject.Inject

class ViewModelA
@AssistedInject constructor(
    val repository: RootRepository,
    @Assisted val handler: SavedStateHandle
): ViewModel() {

    @AssistedInject.Factory
    interface Factory: ViewModelAssistedFactory<ViewModelA>

}