package ru.skillbranch.skillarticles.example

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_test.*
import ru.skillbranch.skillarticles.App
import ru.skillbranch.skillarticles.R
import ru.skillbranch.skillarticles.data.local.PrefManager
import ru.skillbranch.skillarticles.di.components.DaggerActivityComponent
import ru.skillbranch.skillarticles.di.modules.ActivityModule
import javax.inject.Inject

class TestActivity: AppCompatActivity() {

    @Inject
    lateinit var injectPair: Pair<String, String>

    @Inject
    lateinit var preferences: PrefManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test)

        App.activityComponent = DaggerActivityComponent.builder()
            .activityModule(ActivityModule(this))
            .appComponent(App.appComponent)
            .build()

        injectDependency()

        btn_a.setOnClickListener {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, FragmentA())
                .commit()
        }
        btn_b.setOnClickListener {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, FragmentA())
                .commit()
        }
        btn_clear.setOnClickListener {
            val old = supportFragmentManager.findFragmentById(R.id.container)
            if (old != null) {
                supportFragmentManager.beginTransaction()
                    .remove(old)
                    .commit()
            }
        }

    }

    fun injectDependency() {
        App.activityComponent.inject(this)
    }

}