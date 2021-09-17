package ru.skillbranch.sbdelivery.screens.demo.logic

fun DemoFeature.State.reduce(msg: DemoFeature.Msg): Pair<DemoFeature.State, Set<DemoFeature.Eff>> =
    when (msg) {
        is DemoFeature.Msg.Clear -> copy(count = 0, isLoading = false) to emptySet()
        is DemoFeature.Msg.NextRandom -> copy(isLoading = true) to setOf(DemoFeature.Eff.NextGenerate)
        is DemoFeature.Msg.ShowValue -> copy(count = msg.value, isLoading = false) to emptySet()
    }