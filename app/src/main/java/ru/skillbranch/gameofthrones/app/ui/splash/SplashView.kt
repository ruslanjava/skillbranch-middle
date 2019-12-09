package ru.skillbranch.gameofthrones.app.ui.splash

interface SplashView {

    fun showLoading(percent: Int)

    fun showInternetError()

    fun showNextScreen()

}