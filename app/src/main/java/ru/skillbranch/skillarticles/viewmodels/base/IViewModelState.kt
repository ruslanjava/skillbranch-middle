package ru.skillbranch.skillarticles.viewmodels.base

import android.os.Bundle

interface IViewModelState {

    fun saveState(outState: Bundle)
    fun restoreState(savedState: Bundle) : IViewModelState

}