package ru.skillbranch.sbdelivery.screens.demo

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import ru.skillbranch.sbdelivery.screens.demo.ui.DemoScreen

class RootActivity: AppCompatActivity() {

    val vm: DemoViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DemoScreen(vm)
        }
    }

}
