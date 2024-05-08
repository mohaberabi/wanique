package com.mohaberabi.auth.presentation.register.viewmodel

sealed class RegisterAction {


    data object OnTogglePassword : RegisterAction()


    data object OnLoginClicked : RegisterAction()
    data object OnRegisterClicked : RegisterAction()
}