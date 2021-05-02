package ru.skillbranch.skillarticles.example

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_a.*
import ru.skillbranch.skillarticles.R
import ru.skillbranch.skillarticles.data.local.PrefManager
import javax.inject.Inject
import javax.inject.Named

@AndroidEntryPoint
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

    val viewModel: ViewModelA by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Log.e("FragmentA", "viewModelInstance: ${System.identityHashCode(viewModel)}")
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