package ru.skillbranch.sbdelivery.screens.demo.logic

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ru.skillbranch.sbdelivery.screens.root.logic.IEffHandler

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

    fun listen(scope: CoroutineScope, effHandler: IEffHandler<>) {

    }

    data class State(val count: Int = 0, val isLoading: Boolean = false)
    sealed class Msg {
        object Increment: Msg()
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