package ru.skillbranch.skillarticles.ui.auth

import android.os.Bundle
import android.view.View
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.navArgs
import kotlinx.android.synthetic.main.fragment_registration.*
import ru.skillbranch.skillarticles.R
import ru.skillbranch.skillarticles.ui.base.BaseFragment
import ru.skillbranch.skillarticles.viewmodels.auth.AuthViewModel

class RegistrationFragment : BaseFragment<AuthViewModel>() {

    override val viewModel: AuthViewModel by viewModels()
    override val layout: Int = R.layout.fragment_registration
    private val args: RegistrationFragmentArgs by navArgs()

    override fun setupViews() {
        btn_register.isEnabled = false
        btn_register.setOnClickListener { onRegisterClicked() }
        et_name.doAfterTextChanged { viewModel.onNameChanged(it.toString()) }
        et_login.doAfterTextChanged { viewModel.onEmailChanged(it.toString()) }
        et_password.doAfterTextChanged { viewModel.onPasswordChanged(it.toString()) }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.observeValidNames(viewLifecycleOwner, Observer { valid ->
            wrap_name.error = if (valid) null else getString(R.string.auth_invalid_name)
        })
        viewModel.observeValidEmails(viewLifecycleOwner, Observer { valid ->
            wrap_login.error = if (valid) null else getString(R.string.auth_invalid_email)
        })
        viewModel.observeValidPasswords(viewLifecycleOwner, Observer { valid ->
            wrap_password.error = if (valid) null else getString(R.string.auth_invalid_password)
        })

        viewModel.observeEnabledRegistrations(viewLifecycleOwner, Observer { enabled ->
            btn_register.isEnabled = enabled
        })
    }

    private fun onRegisterClicked() {
        viewModel.handleRegister(
            et_name.text.toString(),
            et_login.text.toString(),
            et_password.text.toString(),
            args.privateDestination
        )
    }

}