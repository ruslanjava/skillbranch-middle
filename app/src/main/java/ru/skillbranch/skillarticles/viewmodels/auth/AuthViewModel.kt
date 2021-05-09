package ru.skillbranch.skillarticles.viewmodels.auth

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.skillbranch.skillarticles.App
import ru.skillbranch.skillarticles.R
import ru.skillbranch.skillarticles.data.repositories.IRootRepository
import ru.skillbranch.skillarticles.viewmodels.base.BaseViewModel
import ru.skillbranch.skillarticles.viewmodels.base.IViewModelState
import ru.skillbranch.skillarticles.viewmodels.base.NavigationCommand
import ru.skillbranch.skillarticles.viewmodels.base.Notify

@AndroidEntryPoint
class AuthViewModel @ViewModelInject constructor(
    @Assisted handle: SavedStateHandle,
    val repository: IRootRepository
): BaseViewModel<AuthState>(handle, AuthState()), IAuthViewModel {

    private val validNames = MutableLiveData<Boolean>()
    private val validEmails = MutableLiveData<Boolean>()
    private val validPasswords = MutableLiveData<Boolean>()
    private val enabledRegistrations = CombinedLiveData(CombinedLiveData(validNames, validEmails), validPasswords)

    init {
        subscribeOnDataSource(repository.isAuth()) { isAuth, state ->
            state.copy(isAuth = isAuth)
        }
    }

    override fun handleLogin(login: String, pass: String, dest: Int?) {
        launchSafely {
            repository.login(login, pass)
            withContext(Dispatchers.Main) {
                navigate(NavigationCommand.FinishLogin(dest))
            }
        }
    }

    override fun handleRegister(name: String, login: String, password: String, dest: Int?) {
        if (name.isBlank() || login.isBlank() || password.isBlank()) {
            notify(Notify.ErrorMessage(App.applicationContext().getString(R.string.auth_empty_fields)))
            return
        }
        if (!name.matches(NAME_REGEX)) {
            notify(Notify.ErrorMessage(App.applicationContext().getString(R.string.auth_invalid_name)))
            return
        }
        if (!login.matches(EMAIL_REGEX)) {
            notify(Notify.ErrorMessage(App.applicationContext().getString(R.string.auth_invalid_email)))
            return
        }
        if (!password.matches(PASSWORD_REGEX)) {
            notify(Notify.ErrorMessage(App.applicationContext().getString(R.string.auth_invalid_password)))
            return
        }

        launchSafely {
            repository.register(name, login, password)
            withContext(Dispatchers.Main) {
                navigate(NavigationCommand.FinishLogin(dest))
            }
        }
    }

    fun observeValidNames(owner: LifecycleOwner, observer: Observer<Boolean>) {
        validNames.observe(owner, observer)
    }

    fun observeValidEmails(owner: LifecycleOwner, observer: Observer<Boolean>) {
        validEmails.observe(owner, observer)
    }

    fun observeValidPasswords(owner: LifecycleOwner, observer: Observer<Boolean>) {
        validPasswords.observe(owner, observer)
    }

    fun observeEnabledRegistrations(owner: LifecycleOwner, observer: Observer<Boolean>) {
        enabledRegistrations.observe(owner, observer)
    }

    fun onNameChanged(name: String) {
        GlobalScope.launch(Dispatchers.IO) {
            validNames.postValue(NAME_REGEX.matches(name))
        }
    }

    fun onEmailChanged(email: String) {
        GlobalScope.launch(Dispatchers.IO) {
            validEmails.postValue(EMAIL_REGEX.matches(email))
        }
    }

    fun onPasswordChanged(password: String) {
        GlobalScope.launch(Dispatchers.IO) {
            validPasswords.postValue(PASSWORD_REGEX.matches(password))
        }
    }

    class CombinedLiveData(aData: LiveData<Boolean>, bData: LiveData<Boolean>) : MediatorLiveData<Boolean>() {

        private var a: Boolean? = null
        private var b: Boolean? = null

        init {
            addSource(aData) { a: Boolean ->
                this.a = a
                if (b != null) {
                    this.postValue(a && b == true)
                } else {
                    this.postValue(false)
                }
            }
            addSource(bData) { b: Boolean ->
                this.b = b
                if (a != null) {
                    this.postValue(a == true && b)
                } else {
                    this.postValue(false)
                }
            }
        }
    }

    companion object {

        // имя не короче 3 символов
        val NAME_REGEX ="^[A-Za-z0-9]{3,}\$".toRegex()

        // пароль не короче 8 символов без спецзнаков - только буквы и цифры
        val PASSWORD_REGEX ="^[A-Za-z0-9]{8,}\$".toRegex()

        // login - email пользователя
        val EMAIL_REGEX = android.util.Patterns.EMAIL_ADDRESS.toRegex()

    }

}

data class AuthState(val isAuth: Boolean = false): IViewModelState