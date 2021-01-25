package ru.skillbranch.skillarticles.example

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_a.*
import ru.skillbranch.skillarticles.App
import ru.skillbranch.skillarticles.R
import ru.skillbranch.skillarticles.data.local.PrefManager
import ru.skillbranch.skillarticles.di.components.DaggerFragmentAComponent
import ru.skillbranch.skillarticles.di.modules.FragmentAModule
import javax.inject.Inject
import javax.inject.Named

class FragmentA : Fragment() {

    @Inject
    @Named("dep1")
    lateinit var dependency1: String

    @Inject
    @Named("dep2")
    lateinit var dependency2: String

    @Inject
    lateinit var prefs: PrefManager

    @Inject
    lateinit var activity: TestActivity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        DaggerFragmentAComponent.builder().activityComponent(App.activityComponent)
            .fragmentAModule(FragmentAModule())
            .build()
            .inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_a, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tv_activity.text = "${activity.title} ${System.identityHashCode(activity)}"
        tv_prefs.text = "prefs: ${System.identityHashCode(prefs)}"
        tv_dep1.text = "dep1: $dependency1"
        tv_dep2.text = "dep2: $dependency2"
    }

}