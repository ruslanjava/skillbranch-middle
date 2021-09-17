package ru.skillbranch.sbdelivery.screens.demo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ru.skillbranch.sbdelivery.screens.demo.logic.DemoEffHandler
import ru.skillbranch.sbdelivery.screens.demo.logic.DemoFeature
import ru.skillbranch.sbdelivery.screens.demo.logic.IEffHandler

class DemoViewModel(): ViewModel() {

    val feature = DemoFeature

    init {
        val handler: IEffHandler<DemoFeature.Eff, DemoFeature.Msg> = DemoEffHandler()
        feature.listen(viewModelScope, handler)
    }

    fun accept(msg: DemoFeature.Msg) {

    }

}