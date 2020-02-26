package ru.skillbranch.gameofthrones.ui

import android.app.Application
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_root.*
import ru.skillbranch.gameofthrones.R
import ru.skillbranch.gameofthrones.ui.splash.SplashFragmentDirections
import java.lang.IllegalArgumentException

class RootActivity : AppCompatActivity() {

    private lateinit var viewModel: RootViewModel
    lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_root)
        initViewModel()

        savedInstanceState ?: prepareData()
        navController = Navigation.findNavController(
            this,
            R.id.nav_host_fragment
        )
    }

    override fun onOptionsItemSelected(item: MenuItem) : Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                navController.popBackStack()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun prepareData() {
        viewModel.syncDataIfNeed().observe(this, Observer<LoadResult<Boolean>> {
            when (it) {

                is LoadResult.Loading -> {
                    navController.navigate(R.id.nav_splash)
                }

                is LoadResult.Success -> {
                    val action = SplashFragmentDirections.actionNavSplashToNavHouses()
                    navController.navigate(action)
                }

                is LoadResult.Error -> {
                    Snackbar.make(
                        root_container, it.errorMessage.toString(), Snackbar.LENGTH_INDEFINITE
                    ).show()
                }

            }
        })
    }

    private fun initViewModel() {
        val vmFactory = RootViewModelFactory(this.application)
        viewModel = ViewModelProviders.of(this, vmFactory).get(RootViewModel::class.java)
    }

    class RootViewModelFactory(private val app: Application) : ViewModelProvider.Factory {

        override fun <T: ViewModel> create(modelClass: Class<T>) : T {
            if (modelClass.isAssignableFrom(RootViewModel::class.java)) {
                return RootViewModel(app) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }

    }

}