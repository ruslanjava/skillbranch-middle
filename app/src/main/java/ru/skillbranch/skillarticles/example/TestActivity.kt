package ru.skillbranch.skillarticles.example

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_test.*
import ru.skillbranch.skillarticles.R
import ru.skillbranch.skillarticles.di.components.ActivityComponent
import ru.skillbranch.skillarticles.di.modules.ActivityModule
import javax.inject.Inject

class TestActivity: AppCompatActivity() {

    private lateinit var activityComponent: ActivityComponent

    @Inject
    lateinit var injectPair: Pair<String, String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_root)

        btn_inject.setOnClickListener {
            injectDependency()
        }

        activityComponent = DaggerActivityComponent.builder()
            .activityModule(ActivityModule())
            .build()
    }

    fun injectDependency() {
        activityComponent.inject(this)

        tv_text.text = "inject field value: ${injectPair.first} ${injectPair.second} instance:${System.identityHashCode(injectPair)}"
    }

}