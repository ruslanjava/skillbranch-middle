package ru.skillbranch.sbdelivery.domain.filter

import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.subjects.BehaviorSubject
import ru.skillbranch.sbdelivery.domain.entity.DishEntity
import ru.skillbranch.sbdelivery.repository.DishesRepositoryContract
import ru.skillbranch.sbdelivery.repository.error.EmptyDishesError

class CategoriesFilterUseCase(private val repository: DishesRepositoryContract) : CategoriesFilter {

    override fun categoryFilterDishes(categoryId: String): Single<List<DishEntity>> {
        return repository.getCachedDishes()
            .map { dishes ->
                if (categoryId.isEmpty()) {
                    return@map dishes
                }
                return@map dishes.filter { dish ->
                    dish.categoryId == categoryId
                }
            }
            .doOnSuccess { result ->
                if (result.isEmpty()) {
                    throw EmptyDishesError("test error")
                }
            }
    }

}