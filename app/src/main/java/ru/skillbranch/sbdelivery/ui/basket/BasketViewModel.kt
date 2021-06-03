package ru.skillbranch.sbdelivery.ui.basket

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import ru.skillbranch.sbdelivery.core.BaseViewModel
import ru.skillbranch.sbdelivery.core.notifier.BasketNotifier
import ru.skillbranch.sbdelivery.core.notifier.event.BasketEvent

class BasketViewModel(
    private val notifier: BasketNotifier
) : BaseViewModel() {

    val liveData = MutableLiveData<BasketEvent>()

    fun observeEvents(owner: LifecycleOwner, observer: Observer<BasketEvent>) {
        notifier.eventSubscribe()
            .subscribe {
                liveData.postValue(it)
            }
        liveData.observe(owner, observer)
    }

}