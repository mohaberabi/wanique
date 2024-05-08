package com.mohaberabi.runique.core.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navDeepLink
import androidx.navigation.navigation
import com.mohaberabi.auth.presentation.intro.screen.IntroScreenRoot
import com.mohaberabi.auth.presentation.login.screen.LoginScreenRoot
import com.mohaberabi.auth.presentation.register.screen.RegisterScreenRoot
import com.mohaberabi.core.notification.ActiveRunService
import com.mohaberabi.run.presentation.activerun.screen.ActiveRunScreenRoot
import com.mohaberabi.run.presentation.overview.screen.RunOverViewRoot
import com.mohaberabi.runique.MainActivity


@Composable
fun RuniqueNavGraph(
    navController: NavHostController,
    isLoggedIn: Boolean,
    onActivityClick: () -> Unit
) {
    NavHost(
        navController = navController,
        startDestination = if (isLoggedIn) RuniqueRoutes.RUN_NAVIGATION else RuniqueRoutes.AUTH_NAVIGATION
    ) {
        authNav(navController)
        runNav(
            navController,
            onActivityClick
        )
    }
}


private fun NavGraphBuilder.authNav(navController: NavHostController) {

    navigation(
        startDestination = RuniqueRoutes.IntroScreen.name,
        route = RuniqueRoutes.AUTH_NAVIGATION
    ) {
        composable(
            route = RuniqueRoutes.IntroScreen.name
        ) {
            IntroScreenRoot(
                onSignIn = {
                    navController.navigate(RuniqueRoutes.LoginScreen.name)

                },
                onSignUp = {
                    navController.navigate(RuniqueRoutes.RegisterScreen.name)
                },
            )

        }
        composable(
            route = RuniqueRoutes.RegisterScreen.name,
        ) {
            RegisterScreenRoot(
                onRegisterDone = {

                    navController.popBackStack()
                    navController.navigate(RuniqueRoutes.LoginScreen.name)

                },
                onSignIn = {
                    navController.navigate(RuniqueRoutes.LoginScreen.name) {
                        popUpTo(RuniqueRoutes.RegisterScreen.name) {
                            inclusive = true
                            saveState = true
                        }
                        restoreState = true
                    }

                }
            )

        }
        composable(RuniqueRoutes.LoginScreen.name) {


            LoginScreenRoot(
                onLoginDone = {

                    navController.popBackStack()
                    navController.navigate(RuniqueRoutes.RunOverView.name)
                },
                onRegister = {
                    navController.navigate(RuniqueRoutes.RegisterScreen.name) {
                        popUpTo(RuniqueRoutes.LoginScreen.name) {
                            inclusive = true
                            saveState = true
                        }
                        restoreState = true
                    }
                }
            )
        }
    }
}


private fun NavGraphBuilder.runNav(
    navController: NavController,
    onActivityClick: () -> Unit,
) {


    navigation(
        startDestination = RuniqueRoutes.RunOverView.name,
        route = RuniqueRoutes.RUN_NAVIGATION,

        ) {
        composable(RuniqueRoutes.RunOverView.name) {

            RunOverViewRoot(
                onAnalyticsClick = onActivityClick,
                onLogout = {
                    navController.navigate(RuniqueRoutes.AUTH_NAVIGATION) {
                        popUpTo(RuniqueRoutes.RUN_NAVIGATION) {
                            inclusive = true
                        }
                    }
                },
                onStartRunClick = {


                    navController.navigate(RuniqueRoutes.ActiveRun.name)
                }
            )
        }
        composable(
            route = RuniqueRoutes.ActiveRun.name,

            deepLinks = listOf(
                navDeepLink {
                    uriPattern =
                        ActiveRunService.ACTIVE_RUN_DEEP_LINK
                }
            )
        ) {
            val context = LocalContext.current

            ActiveRunScreenRoot(
                onBack = {
                    navController.navigateUp()
                },
                onRunDone = {
                    navController.navigateUp()
                },
                onServiceToggle = { shouldServiceRun ->
                    if (shouldServiceRun) {
                        context.startService(
                            ActiveRunService.createStartIntent(
                                context,
                                MainActivity::class.java
                            )
                        )
                    } else {
                        context.startService(
                            ActiveRunService.createStopIntent(
                                context
                            )
                        )
                    }
                }
            )
        }
    }
}