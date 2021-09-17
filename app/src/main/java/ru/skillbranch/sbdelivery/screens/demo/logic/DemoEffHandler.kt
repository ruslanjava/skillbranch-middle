package ru.skillbranch.sbdelivery.screens.demo.logic

import android.util.Log
import kotlinx.coroutines.delay

class DemoEffHandler: IEffHandler<DemoFeature.Eff, DemoFeature.Msg> {

    override suspend fun handle(effect: DemoFeature.Eff, commit: (DemoFeature.Msg) -> Unit) {
        Log.e("DemoEffHandler", "EFF $effect")
        when (effect) {
            is DemoFeature.Eff.NextGenerate -> {
                delay(3000L)
                val rnd = (0..100).random()
                commit(DemoFeature.Msg.ShowValue(rnd))
            }
        }
    }

}