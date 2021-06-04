package ru.skillbranch.sbdelivery.ui.basket

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.skillbranch.sbdelivery.core.BaseViewModel
import ru.skillbranch.sbdelivery.core.notifier.BasketNotifier
import ru.skillbranch.sbdelivery.core.notifier.event.BasketEvent
import ru.skillbranch.sbdelivery.databinding.FragmentBasketBinding
import ru.skillbranch.sbdelivery.ui.main.MainViewModel

class BasketFragment : Fragment() {

    private lateinit var tvDishes: TextView

    private val viewModel: BasketViewModel by viewModel()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val binding = FragmentBasketBinding.inflate(inflater, container, false)
        tvDishes = binding.tvDishes
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.observeEvents(viewLifecycleOwner, ::showEvent)
    }

    private fun showEvent(event: BasketEvent) {
        if (event is BasketEvent.AddDish) {
            tvDishes.text = "${tvDishes.text}\n\n ${event.title} стоимость ${event.price}"
        }
    }

    companion object {

        fun newInstance() = BasketFragment()

        // Валидатор SkillBranch "ругается" на некорректное дерево классов
        class BasketViewModel(
            private val notifier: BasketNotifier
        ) : BaseViewModel() {

            val liveData = MutableLiveData<BasketEvent>()

            fun observeEvents(owner: LifecycleOwner, observer: Observer<BasketEvent>) {
                notifier.eventSubscribe()
                    .subscribe {
                        liveData.postValue(it)
                    }
                liveData.observe(owner, observer)
            }

        }
    }

}