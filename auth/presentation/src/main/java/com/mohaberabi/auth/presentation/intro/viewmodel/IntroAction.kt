package com.mohaberabi.auth.presentation.intro.viewmodel

sealed class IntroAction {


    data object OnSignInClick : IntroAction()


    data object OnSignUpClick : IntroAction()
}