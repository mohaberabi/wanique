package com.mohaberabi.auth.presentation.login.screen

import RuniquePasswordTextField
import android.widget.Toast
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource

import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

import com.mohaberabi.auth.presentation.R
import com.mohaberabi.auth.presentation.login.viewmodel.LoginAction
import com.mohaberabi.auth.presentation.login.viewmodel.LoginEvent
import com.mohaberabi.auth.presentation.login.viewmodel.LoginState
import com.mohaberabi.auth.presentation.login.viewmodel.LoginViewModel

import com.mohaberabi.core.presentation.designsystem.compose.GradientBg
import com.mohaberabi.core.presentation.designsystem.compose.RuniqueButton
import com.mohaberabi.core.presentation.designsystem.compose.RuniqueTextField

import com.mohaberabi.core.presentation.designsystem.theme.EmailIcon

import com.mohaberabi.core.presentation.designsystem.theme.RuniqueTheme
import com.mohaberabi.core.presentation.ui.compose.ObserveAsEvent
import org.koin.androidx.compose.koinViewModel

@Composable
fun LoginScreenRoot(
    viewmodel: LoginViewModel = koinViewModel(),
    onLoginDone: () -> Unit,
    onRegister: () -> Unit,
) {
    val context = LocalContext.current
    val keyBoardController = LocalSoftwareKeyboardController.current

    ObserveAsEvent(viewmodel.event) {

            event ->
        when (event) {

            is LoginEvent.Error -> {
                keyBoardController?.hide()
                Toast.makeText(
                    context,
                    event.error.asString(context), Toast.LENGTH_SHORT
                ).show()
            }

            is LoginEvent.Done -> {
                Toast.makeText(
                    context,
                    R.string.registration_done, Toast.LENGTH_SHORT
                ).show()
                onLoginDone()
            }

        }
    }
    val state = viewmodel.state.collectAsState().value
    LoginScreen(
        state = state,
        onAction = { action ->
            when (action) {
                is LoginAction.OnRegisterClick -> onRegister()
                else->Unit
            }
            viewmodel.onAction(action)
        }
    )


}


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun LoginScreen(
    state: LoginState,
    onAction: (LoginAction) -> Unit = {},

    ) {

    val typo = MaterialTheme.typography

    GradientBg {

        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .fillMaxSize()
                .padding(16.dp)
                .padding(top = 48.dp)
        ) {

            Text(
                text = "Hi There ",
                style = typo.headlineMedium
            )
            Text(
                text = "Welcome to ultimate running companion! Track , analyze and run  ",
                style = typo.bodySmall
            )



            Spacer(modifier = Modifier.height(48.dp))

            RuniqueTextField(
                state = state.email,
                hint = stringResource(R.string.email_hint),
                title = stringResource(R.string.email),
                startIcon = EmailIcon,
                modifier = Modifier.fillMaxWidth(),
                keyboardType = KeyboardType.Email,
            )
            Spacer(modifier = Modifier.height(48.dp))
            RuniquePasswordTextField(
                state = state.password,
                hint = "********",
                title = stringResource(R.string.password),
                isPassword = !state.isPasswordVisible,
                modifier = Modifier.fillMaxWidth(),
                onTogglePassword = {
                    onAction(LoginAction.OnTogglePassword)
                }
            )
            Spacer(modifier = Modifier.height(20.dp))

            RuniqueButton(
                enabled = state.canLogin,
                label = stringResource(id = R.string.login),
                onClick = {
                    onAction(LoginAction.OnLoginClick)
                },
                isLoading = state.isLoggingIn
            )
            TextButton(
                onClick = {
                    onAction(LoginAction.OnRegisterClick)
                },
            ) {

                Text(
                    text = stringResource(id = R.string.create_account),
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }

}


@OptIn(ExperimentalFoundationApi::class)

@Preview(showBackground = true)
@Composable
fun PreviewLoginscreen() {

    RuniqueTheme {

        LoginScreen(state = LoginState())
    }
}
