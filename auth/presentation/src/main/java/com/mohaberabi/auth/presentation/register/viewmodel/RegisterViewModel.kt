package com.mohaberabi.auth.presentation.register.viewmodel

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
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)

class RegisterViewModel(
    private val userDataValidator: UserDataValidator,
    private val authRepository: AuthRepository,
) : ViewModel() {
    private val _state = MutableStateFlow(RegisterState())


    val state: StateFlow<RegisterState>
        get() = _state


    private val _event = Channel<RegisterEvent>()
    val event = _event.receiveAsFlow()

    init {

        _state.value.email.textAsFlow().onEach { email ->
            _state.update {
                val isValidEmail = userDataValidator.isValidEmail(email.toString())
                it.copy(
                    isEmailValid = isValidEmail,
                    canRegister = isValidEmail && _state.value.passwordValidState.isValid
                )
            }
        }.launchIn(viewModelScope)
        _state.value.password.textAsFlow().onEach { password ->
            _state.update {
                val passWordState = userDataValidator.validatePassword(password.toString())
                it.copy(
                    passwordValidState = passWordState,
                    canRegister = passWordState.isValid && _state.value.isEmailValid
                )
            }
        }.launchIn(viewModelScope)
    }


    fun onAction(action: RegisterAction) {


        when (action) {
            is RegisterAction.OnRegisterClicked -> register()
            is RegisterAction.OnTogglePassword -> {
                _state.update {
                    it.copy(
                        isPasswordVisible = !it.isPasswordVisible
                    )
                }
            }

            else -> Unit
        }
    }

    private fun register() {
        viewModelScope.launch {
            _state.update {
                it.copy(
                    isRegistering = true
                )

            }
            val result =
                authRepository.register(
                    _state.value.email.text.toString().trim(),
                    _state.value.password.text.toString().trim()
                )

            when (result) {
                is AppResult.Error -> {
                    if (result.error == DataError.Network.CONFLICT) {

                        _event.send(RegisterEvent.Error(UiText.StringResource(R.string.user_already_exist)))

                    } else {
                        _event.send(RegisterEvent.Error(result.error.asUiText()))

                    }
                }

                is AppResult.Done -> {
                    _event.send(RegisterEvent.Done)

                }
            }
            _state.update {
                it.copy(
                    isRegistering = false
                )

            }

        }
    }
}