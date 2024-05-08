package com.mohaberabi.runique.core.navigation

sealed class RuniqueRoutes(val name: String) {


    data object IntroScreen : RuniqueRoutes("intro")
    data object RegisterScreen : RuniqueRoutes("register")
    data object LoginScreen : RuniqueRoutes("login")
    data object RunOverView : RuniqueRoutes("runOverview")
    data object ActiveRun : RuniqueRoutes("activeRun")


    companion object {
        const val AUTH_NAVIGATION = "auth"
        const val RUN_NAVIGATION = "run"

    }
}

