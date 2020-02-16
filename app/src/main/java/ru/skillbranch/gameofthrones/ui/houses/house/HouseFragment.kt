package ru.skillbranch.gameofthrones.ui.houses.house

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_house.*
import ru.skillbranch.gameofthrones.R
import ru.skillbranch.gameofthrones.data.local.entities.CharacterItem
import ru.skillbranch.gameofthrones.data.local.entities.HouseType
import ru.skillbranch.gameofthrones.ui.custom.ItemDivider
import ru.skillbranch.gameofthrones.ui.houses.HousesFragmentDirections

/**
 * A placeholder fragment containing a simple view.
 */
class HouseFragment : Fragment() {

    private lateinit var charactersAdapter: CharactersAdapter
    private lateinit var viewModel: HouseViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        val houseName = arguments?.getString(HOUSE_NAME) ?: HouseType.STARK.title

        charactersAdapter = CharactersAdapter {
            val houseType = HouseType.fromString(it.house)
            val action = HousesFragmentDirections.actionNavHousesToNavCharacter(it.id, houseType.title, it.name)
            findNavController().navigate(action)
        }

        val vmFactory = HouseViewModelFactory(houseName)
        viewModel = ViewModelProviders.of(this, vmFactory).get(HouseViewModel::class.java)
        viewModel.getCharacters().observe(this, Observer<List<CharacterItem>> { items ->
            charactersAdapter.updateItems(items)
        })
        viewModel.handleSearchQuery("")
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        with(menu.findItem(R.id.action_search).actionView as SearchView) {
            setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String): Boolean {
                    viewModel.handleSearchQuery(query)
                    return true
                }
                override fun onQueryTextChange(newText: String): Boolean {
                    viewModel.handleSearchQuery(newText)
                    return true
                }
            })
        }
        super.onPrepareOptionsMenu(menu)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_house, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(rv_characters_list) {
            layoutManager = LinearLayoutManager(context)
            addItemDecoration(ItemDivider())
            adapter = charactersAdapter
        }
    }

    companion object {

        private const val HOUSE_NAME = "house_name"

        @JvmStatic
        fun newInstance(houseName: String) : HouseFragment {
            return HouseFragment().apply {
                arguments = bundleOf(HOUSE_NAME to houseName)
            }
        }

    }

}