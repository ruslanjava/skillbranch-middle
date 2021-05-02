package ru.skillbranch.skillarticles.example

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_a.tv_activity
import kotlinx.android.synthetic.main.fragment_b.*
import ru.skillbranch.skillarticles.R
import javax.inject.Inject

@AndroidEntryPoint
class FragmentB : Fragment() {

    @Inject
    lateinit var activity: TestActivity
    @Inject
    lateinit var isBigText: String

    val viewModel: ViewModelA by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Log.e("FragmentB", "viewModelInstance: ${System.identityHashCode(viewModel)}")
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
        tv_is_big_text.text = "isBigText: $isBigText"
    }

}