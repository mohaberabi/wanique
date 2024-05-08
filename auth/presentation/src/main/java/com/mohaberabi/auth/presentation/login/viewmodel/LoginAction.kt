package com.mohaberabi.auth.presentation.login.viewmodel


sealed class LoginAction {

    data object OnRegisterClick : LoginAction()
    data object OnLoginClick : LoginAction()
    data object OnTogglePassword : LoginAction()

}