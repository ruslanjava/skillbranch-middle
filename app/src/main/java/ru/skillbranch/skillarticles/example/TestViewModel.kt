package ru.skillbranch.skillarticles.example

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.squareup.inject.assisted.Assisted
import com.squareup.inject.assisted.AssistedInject
import ru.skillbranch.skillarticles.data.repositories.RootRepository
import javax.inject.Inject

class TestViewModel
@AssistedInject constructor(
        rootRepository: RootRepository,
        @Assisted val handler: SavedStateHandle
): ViewModel() {

    @AssistedInject.Factory
    interface Factory: ViewModelAssistedFactory<ViewModelB>

}