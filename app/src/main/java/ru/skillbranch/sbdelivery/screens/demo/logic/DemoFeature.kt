package ru.skillbranch.sbdelivery.screens.demo.logic

import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

object DemoFeature {

    fun initialState(): State = State()
    fun initialEffects(): Set<Eff> = emptySet()

    private val _state: MutableStateFlow<State> = MutableStateFlow(initialState())
    val state
    get() = _state.asStateFlow()
    private lateinit var _scope: CoroutineScope

    private val mutations: MutableSharedFlow<Msg> = MutableSharedFlow()

    fun mutate(mutation: Msg) {
        _scope.launch {
            mutations.emit(mutation)
        }
    }

    fun listen(scope: CoroutineScope, effHandler: IEffHandler<Eff, Msg>) {
        _scope = scope
        _scope.launch {
            mutations
                .onEach { Log.e("DemoEffHandler", "EFF $it") }
                .scan(initialState() to initialEffects()) { (s, _), m ->
                    // reduce state
                    s.reduce(m)
                }
                .collect { (s, eff) ->
                    _state.emit(s)
                    eff.forEach {
                        launch {
                            effHandler.handle(it, ::mutate)
                        }
                    }
                }
        }
    }

    data class State(val count: Int = 0, val isLoading: Boolean = false)

    sealed class Msg {
        object NextRandom: Msg()
        object Clear: Msg()
        data class ShowValue(val value: Int) : Msg()
    }
    sealed class Eff {
        object NextGenerate: Eff()
    }

}

interface IEffHandler<E, M> {
    suspend fun handle(effect: E, commit: (M) -> Unit)
}