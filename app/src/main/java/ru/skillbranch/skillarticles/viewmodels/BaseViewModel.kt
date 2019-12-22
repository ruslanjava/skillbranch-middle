package ru.skillbranch.skillarticles.viewmodels

import androidx.annotation.UiThread
import androidx.annotation.VisibleForTesting
import androidx.lifecycle.*

abstract class BaseViewModel<T>(initState: T) : ViewModel() {

    @VisibleForTesting(otherwise = VisibleForTesting.PROTECTED)
    val notifications: MutableLiveData<Event<Notify>> = MutableLiveData()

    @VisibleForTesting(otherwise = VisibleForTesting.PROTECTED)
    val state: MediatorLiveData<T> = MediatorLiveData<T>().apply {
        value = initState
    }

    // not null current state
    protected val currentState
        get() = state.value!!

    // лямбда выражение принимает в качестве аргумента лямьду в котору передается текущее состояние
    // и она возвращает модифицированное состояние, которое присуваивается текущему состоянию
    @UiThread
    protected inline fun updateState(update: (currentState: T) -> T) {
        val updatedState : T = update(currentState)
        state.value = updatedState
    }

    @UiThread
    protected fun notify(content: Notify) {
        notifications.value = Event(content)
    }

    // более компактная форма записи observe принимает последним аргументом лямбда выражение
    // обрабатывающее изменение текущего состояния
    fun observeState(owner: LifecycleOwner, onChanged: (newState : T) -> Unit) {
        state.observe(owner, Observer { onChanged(it!!)})
    }

    // более компактная форма записи observe вызывает лямбда выражение обарбоатчик только в том случае,
    // если сообщение не было уже обработано, реализует данное поведение благодаря EventObserver
    fun observeNotifications(owner: LifecycleOwner, onNotify: (notification: Notify) -> Unit) {
        notifications.observe(owner, EventObserver {onNotify(it)} )
    }

    // функция принимает источник данных и лямбда выражение, обрабатывающее поступающие данные
    // лямбда принимает новые данные и текущее состояние, изменяет его и возвращает модифицированное
    // состояние устанавливается как текущее
    protected fun <S> subscribeOnDataSource(
            source: LiveData<S>,
            onChanged: (newValue: S, currentState: T) -> T?
    ) {
        state.addSource(source) {
            state.value = onChanged(it, currentState) ?: return@addSource
        }
    }

    class ViewModelFactory(private val params: String) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(ArticleViewModel::class.java)) {
                return ArticleViewModel(params) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }

}

class Event<out E>(private val content: E) {

    var hasBeenHandled = false

    @VisibleForTesting(otherwise = VisibleForTesting.NONE)
    fun peekContent(): E {
        return content
    }

    // возвращает контент, который еще не был обработан, иначе Null
    fun getContentIfNotHandled(): E? {
        return if (hasBeenHandled) null
        else {
            hasBeenHandled = true
            content
        }
    }

}

class EventObserver<E>(private val eventContentHandler: (E) -> Unit) : Observer<Event<E>> {

    // в качестве аргумента принимает лямбда выражение обработчик в которую передается необработанное
    // ранее событие получаемое в реализации метода Observer-а onChanged
    override fun onChanged(event: Event<E>?) {
        // если есть необработанное событие (контент) передай в качестве аргумента в лямбду
        event?.getContentIfNotHandled()?.let {
            eventContentHandler.invoke(it)
        }
    }

}

sealed class Notify(val message: String) {
    data class TextMessage(val msg: String) : Notify(msg)

    data class ActionMessage(
            val msg: String,
            val actionLabel: String,
            val actionHandler: (() -> Unit)?
    ): Notify(msg)

    data class ErrorMessage(
            val msg: String,
            val errLabel: String,
            val errHandler: (() -> Unit)?
    ): Notify(msg)

}


