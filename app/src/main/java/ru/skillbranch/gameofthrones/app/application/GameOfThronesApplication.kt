package ru.skillbranch.gameofthrones.app.application

import android.app.Application
import android.content.Context

class GameOfThronesApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        context = this
    }

    companion object {

        lateinit var context: Context

    }

}