package com.mohaberabi.auth.presentation.login.viewmodel

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.text2.input.textAsFlow
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mohaberabi.auth.domain.repository.AuthRepository
import com.mohaberabi.auth.domain.validators.UserDataValidator
import com.mohaberabi.auth.presentation.R
import com.mohaberabi.core.domain.utils.AppResult
import com.mohaberabi.core.domain.utils.error.DataError
import com.mohaberabi.core.presentation.ui.util.UiText
import com.mohaberabi.core.presentation.ui.util.asUiText
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
class LoginViewModel(
    private val authRepository: AuthRepository,
    private val userDataValidator: UserDataValidator,
) : ViewModel() {
    private val _event = Channel<LoginEvent>()
    val event = _event.receiveAsFlow()
    private val _state = MutableStateFlow(LoginState())
    val state = _state.asStateFlow()


    init {
        combine(
            _state.value.email.textAsFlow(),
            _state.value.password.textAsFlow()
        ) { email, password ->
            _state.update {
                it.copy(
                    canLogin = userDataValidator.isValidEmail(email.toString())
                            && password.isNotEmpty()
                )
            }
        }.launchIn(viewModelScope)
    }

    fun onAction(action: LoginAction) {

        when (action) {
            LoginAction.OnLoginClick -> login()
            LoginAction.OnTogglePassword -> _state.update {
                it.copy(
                    isPasswordVisible = !it.isPasswordVisible
                )
            }

            else -> Unit
        }
    }

    private fun login() {

        _state.update {
            it.copy(
                isLoggingIn = true
            )
        }

        viewModelScope.launch {
            val result = authRepository.login(
                _state.value.email.text.toString(),
                _state.value.password.text.toString()
            )

            when (result) {
                is AppResult.Error -> {
                    if (result.error == DataError.Network.UNAUTHORIZED) {
                        _event.send(LoginEvent.Error(UiText.StringResource(R.string.worng_email_paassword)))
                    } else {
                        _event.send(LoginEvent.Error(result.error.asUiText()))

                    }
                }

                is AppResult.Done -> {
                    _event.send(LoginEvent.Done)
                }
            }
            _state.update {
                it.copy(
                    isLoggingIn = false
                )
            }
        }
    }
}