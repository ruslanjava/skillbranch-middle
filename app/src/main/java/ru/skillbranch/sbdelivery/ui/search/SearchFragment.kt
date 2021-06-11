package ru.skillbranch.sbdelivery.ui.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.jakewharton.rxbinding4.appcompat.queryTextChanges
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.skillbranch.sbdelivery.R
import ru.skillbranch.sbdelivery.core.adapter.ProductDelegate
import ru.skillbranch.sbdelivery.core.decor.GridPaddingItemDecoration
import ru.skillbranch.sbdelivery.databinding.FragmentSearchBinding
import ru.skillbranch.sbdelivery.repository.error.EmptyDishesError

class SearchFragment : Fragment() {
    companion object {
        fun newInstance() = SearchFragment()
    }

    private val viewModel: SearchViewModel by viewModel()
    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!
    private val adapter by lazy {
        ProductDelegate().createAdapter {
            // TODO handle click
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.initState()
        viewModel.state.observe(viewLifecycleOwner, ::renderState)
        binding.rvProductGrid.adapter = adapter
        binding.rvProductGrid.addItemDecoration(GridPaddingItemDecoration(17))
        val searchEvent = binding.searchInput.queryTextChanges().skipInitialValue().map { it.toString() }
        viewModel.setSearchEvent(searchEvent)
    }

    private fun renderState(searchState: SearchState) {
        binding.pb.isVisible = searchState is SearchState.Loading
        binding.rvProductGrid.isVisible = searchState is SearchState.Result
        binding.tvError.isVisible = searchState is SearchState.Error
        binding.btnRetry.isVisible = false

        when (searchState) {
            is SearchState.Loading -> {
            }
            is SearchState.Result -> {
                adapter.items = searchState.items
                adapter.notifyDataSetChanged()
            }
            is SearchState.Error -> {
                if (searchState.errorDescription == EmptyDishesError().message) {
                    binding.tvError.text = requireContext().getString(R.string.empty_list)
                } else {
                    binding.btnRetry.isVisible = true
                }
            }
        }
    }

    override fun onDestroyView() {
        binding.rvProductGrid.adapter = null
        _binding = null
        super.onDestroyView()
    }

}