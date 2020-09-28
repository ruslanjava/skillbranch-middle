package ru.skillbranch.skillarticles.viewmodels.auth

import androidx.lifecycle.SavedStateHandle
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ru.skillbranch.skillarticles.data.repositories.RootRepository
import ru.skillbranch.skillarticles.viewmodels.base.BaseViewModel
import ru.skillbranch.skillarticles.viewmodels.base.IViewModelState
import ru.skillbranch.skillarticles.viewmodels.base.NavigationCommand

class AuthViewModel(handle: SavedStateHandle) : BaseViewModel<AuthState>(handle, AuthState()),
    IAuthViewModel {

    private val repository = RootRepository

    init {
        subscribeOnDataSource(repository.isAuth()) { isAuth, state ->
            state.copy(isAuth = isAuth)
        }
    }

    override fun handleLogin(login: String, pass:String, dest: Int?) {
        launchSafely {
            repository.login(login, pass)
            withContext(Dispatchers.Main) {
                navigate(NavigationCommand.FinishLogin(dest))
            }
        }
    }

    override fun handleRegister(name: String, login: String, password: String, dest: Int?) {
        launchSafely {
            repository.register(name, login, password)
            withContext(Dispatchers.Main) {
                navigate(NavigationCommand.FinishLogin(dest))
            }
        }
    }

}

data class AuthState(val isAuth: Boolean = false): IViewModelState