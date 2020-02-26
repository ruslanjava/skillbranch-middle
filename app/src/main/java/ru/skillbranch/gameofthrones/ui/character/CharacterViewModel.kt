package ru.skillbranch.gameofthrones.ui.character

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import ru.skillbranch.gameofthrones.data.local.entities.CharacterFull
import ru.skillbranch.gameofthrones.extensions.mutableLiveData
import ru.skillbranch.gameofthrones.repositories.RootRepository
import java.lang.IllegalArgumentException

class CharacterViewModel (private val characterId: String) : ViewModel() {

    private val repository = RootRepository

    fun getCharacter(): LiveData<CharacterFull> {
        val characters: MutableLiveData<CharacterFull> = mutableLiveData(null)
        repository.findCharacterFullById(characterId) { character ->
            characters.postValue(character)
        }
        return characters
    }

}

class CharacterViewModelFactory(private val characterId: String) : ViewModelProvider.Factory {

    override fun <T: ViewModel> create(modelClass: Class<T>) : T {
        if (modelClass.isAssignableFrom(CharacterViewModel::class.java)) {
            return CharacterViewModel(characterId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }

}