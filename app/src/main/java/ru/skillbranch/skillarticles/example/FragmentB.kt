package ru.skillbranch.skillarticles.example

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_a.*
import kotlinx.android.synthetic.main.fragment_a.tv_activity
import kotlinx.android.synthetic.main.fragment_b.*
import ru.skillbranch.skillarticles.App
import ru.skillbranch.skillarticles.R
import ru.skillbranch.skillarticles.di.components.DaggerFragmentBComponent
import ru.skillbranch.skillarticles.di.modules.FragmentBModule
import javax.inject.Inject

class FragmentB : Fragment() {

    @Inject
    lateinit var activity: TestActivity
    @Inject
    lateinit var isBigText: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        DaggerFragmentBComponent.builder().activityComponent(App.activityComponent)
            .fragmentBModule(FragmentBModule())
            .build()
            .inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_b, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tv_activity.text = "${activity.title} ${System.identityHashCode(activity)}"
        tv_is_big_text.text = "isBigText: ${System.identityHashCode(isBigText)}"
    }

}