package ru.skillbranch.gameofthrones.app.ui.splash

import io.reactivex.Observable
import io.reactivex.ObservableEmitter
import ru.skillbranch.gameofthrones.HouseName
import ru.skillbranch.gameofthrones.data.remote.res.CharacterRes
import ru.skillbranch.gameofthrones.data.remote.res.HouseRes
import ru.skillbranch.gameofthrones.repositories.RootRepository
import java.util.concurrent.TimeUnit

class SplashModel {

    fun seconds() : Observable<Int> {
        return Observable.create<Int> { emitter ->
            var seconds = 0;
            while (!emitter.isDisposed && seconds < 5) {
                TimeUnit.SECONDS.sleep(1)
                seconds++
                emitter.onNext(seconds)
            }
        }
    }

    fun dataState() : Observable<Boolean> {
        return Observable.create<Boolean> { emitter ->
            RootRepository.isNeedUpdate {
                val needUpdate = it
                if (!needUpdate) {
                    emitter.onNext(true)
                    emitter.onComplete()
                } else {
                    loadData(emitter)
                }
            }
        }
    }

    private fun loadData(emitter: ObservableEmitter<Boolean>) {
        RootRepository.dropDb {
            RootRepository.getNeedHouseWithCharacters(
                HouseName.BARATHEON.fullName,
                HouseName.GREYJOY.fullName,
                HouseName.LANNISTER.fullName,
                HouseName.MARTELL.fullName,
                HouseName.STARK.fullName,
                HouseName.TYRELL.fullName,
                HouseName.TAGRARYEN.fullName
            ) {
                val houses = mutableListOf<HouseRes>()
                val characters = mutableListOf<CharacterRes>()
                for (pair in it) {
                    houses.add(pair.first)
                    characters.addAll()
                }
                RootRepository.insertHouses(houses) {
                    RootRepository.insertCharacters(characters)
                }
            }
        }
    }

}