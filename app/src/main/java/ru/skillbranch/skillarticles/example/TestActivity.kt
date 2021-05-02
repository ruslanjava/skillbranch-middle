package ru.skillbranch.skillarticles.example

import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_test.*
import ru.skillbranch.skillarticles.R

@AndroidEntryPoint
class TestActivity: AppCompatActivity() {

    val viewModel: TestViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test)

        Log.e("TestActivity", "viewModelInstance: ${System.identityHashCode(viewModel)}")

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

}