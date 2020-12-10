package ru.skillbranch.skillarticles.example

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_test.*
import ru.skillbranch.skillarticles.App
import ru.skillbranch.skillarticles.R
import ru.skillbranch.skillarticles.data.local.PrefManager
import javax.inject.Inject

class TestActivity: AppCompatActivity() {

    @Inject
    lateinit var injectPair: Pair<String, String>

    @Inject
    lateinit var preferences: PrefManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_root)

        btn_inject.setOnClickListener {
            injectDependency()
        }
    }

    fun injectDependency() {
        App.activityComponent.inject(this)
        tv_text.text = "isDarkMode: ${preferences.isDarkMode} preferences instance:${System.identityHashCode(preferences)} " +
                "inject field value: ${injectPair.first} ${injectPair.second} pair instance:${System.identityHashCode(injectPair)}"
    }

}