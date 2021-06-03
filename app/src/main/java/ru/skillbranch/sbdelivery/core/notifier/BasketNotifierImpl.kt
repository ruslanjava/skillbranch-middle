package ru.skillbranch.sbdelivery.core.notifier

import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.BehaviorSubject
import io.reactivex.rxjava3.subjects.ReplaySubject
import ru.skillbranch.sbdelivery.core.notifier.event.BasketEvent

class BasketNotifierImpl : BasketNotifier {

    private val events: ReplaySubject<BasketEvent> = ReplaySubject.create()

    override fun eventSubscribe(): Observable<BasketEvent> {
        return events
    }

    override fun putDishes(dish: BasketEvent.AddDish) {
        events.onNext(dish)
    }


}