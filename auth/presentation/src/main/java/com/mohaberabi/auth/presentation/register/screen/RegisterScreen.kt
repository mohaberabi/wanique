package com.mohaberabi.auth.presentation.register.screen

import RuniquePasswordTextField
import android.widget.Toast
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mohaberabi.auth.domain.validators.UserDataValidator
import com.mohaberabi.auth.presentation.R
import com.mohaberabi.auth.presentation.register.viewmodel.RegisterAction
import com.mohaberabi.auth.presentation.register.viewmodel.RegisterEvent
import com.mohaberabi.auth.presentation.register.viewmodel.RegisterState
import com.mohaberabi.auth.presentation.register.viewmodel.RegisterViewModel
import com.mohaberabi.core.presentation.designsystem.compose.GradientBg
import com.mohaberabi.core.presentation.designsystem.compose.RuniqueButton
import com.mohaberabi.core.presentation.designsystem.compose.RuniqueTextField
import com.mohaberabi.core.presentation.designsystem.theme.CheckIcon
import com.mohaberabi.core.presentation.designsystem.theme.CrossIcon
import com.mohaberabi.core.presentation.designsystem.theme.EmailIcon
import com.mohaberabi.core.presentation.designsystem.theme.Poppins
import com.mohaberabi.core.presentation.designsystem.theme.RuniqueGray
import com.mohaberabi.core.presentation.designsystem.theme.RuniqueGreen
import com.mohaberabi.core.presentation.designsystem.theme.RuniqueTheme
import com.mohaberabi.core.presentation.ui.compose.ObserveAsEvent
import org.koin.androidx.compose.koinViewModel


@Composable
fun RegisterScreenRoot(
    viewmodel: RegisterViewModel = koinViewModel(),
    onRegisterDone: () -> Unit,
    onSignIn: () -> Unit

) {
    val context = LocalContext.current
    val keyBoardController = LocalSoftwareKeyboardController.current

    ObserveAsEvent(viewmodel.event) {

            event ->
        when (event) {

            is RegisterEvent.Error -> {
                keyBoardController?.hide()
                Toast.makeText(
                    context,
                    event.error.asString(context), Toast.LENGTH_SHORT
                ).show()
            }

            is RegisterEvent.Done -> {
                Toast.makeText(
                    context,
                    R.string.registration_done, Toast.LENGTH_SHORT
                ).show()
                onRegisterDone()
            }

        }
    }
    val state = viewmodel.state.collectAsState().value
    RegisterScreen(
        state = state,
        onAction = { action ->
            when (action) {
                is RegisterAction.OnLoginClicked -> onSignIn()
                else -> Unit

            }
            viewmodel.onAction(action)
        }
    )


}


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun RegisterScreen(
    state: RegisterState,
    onAction: (RegisterAction) -> Unit = {},

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
                text = stringResource(R.string.create_account),
                style = typo.headlineMedium
            )
            val annotatedString = buildAnnotatedString {
                withStyle(
                    style = SpanStyle(
                        fontFamily = Poppins,
                        color = RuniqueGray,

                        )
                ) {
                    append(stringResource(R.string.already_have_an_account) + " ")
                    pushStringAnnotation(
                        tag = "clickableText",
                        annotation = stringResource(R.string.login)
                    )

                    withStyle(
                        style = SpanStyle(
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.primary, fontFamily = Poppins,

                            )
                    ) {
                        append(stringResource(id = R.string.login))
                    }

                }
            }
            ClickableText(
                text = annotatedString,
                onClick = { offset ->
                    annotatedString.getStringAnnotations(
                        tag = "clickableText",
                        start = offset,
                        end = offset
                    ).firstOrNull()?.let {
                        onAction(RegisterAction.OnLoginClicked)
                    }
                },
            )
            Spacer(modifier = Modifier.height(48.dp))

            RuniqueTextField(
                state = state.email,
                hint = stringResource(R.string.email_hint),
                title = stringResource(R.string.email),
                startIcon = EmailIcon,
                endICon = if (state.isEmailValid) {
                    CheckIcon
                } else null,
                modifier = Modifier.fillMaxWidth(),
                additionalInfo = stringResource(R.string.must_be_a_valid_email),
                keyboardType = KeyboardType.Email,
            )
            Spacer(modifier = Modifier.height(48.dp))
            RuniquePasswordTextField(
                state = state.password,
                hint = "********",
                title = stringResource(R.string.password),
                isPassword = state.isPasswordVisible,
                modifier = Modifier.fillMaxWidth(),
                onTogglePassword = {

                    onAction(RegisterAction.OnTogglePassword)
                }
            )
            Spacer(modifier = Modifier.height(20.dp))
            PasswordRequiers(
                isValid = state.passwordValidState.hasMinLength,
                text = stringResource(
                    R.string.at_least_d_characters,
                    UserDataValidator.MIN_PASSWORD_LENGTH
                )
            )
            PasswordRequiers(
                isValid = state.passwordValidState.hasLower,
                text = stringResource(
                    R.string.at_least_d_lower,
                    1,
                )
            )
            PasswordRequiers(
                isValid = state.passwordValidState.hasUpper,
                text = stringResource(
                    R.string.at_least_d_upper,
                    1,
                )
            )
            PasswordRequiers(
                isValid = state.passwordValidState.hasNumber,
                text = stringResource(
                    R.string.at_least_d_nos,
                    1,
                )
            )
            RuniqueButton(


                enabled = state.canRegister,
                label = stringResource(id = R.string.register),
                onClick = {
                    onAction(RegisterAction.OnRegisterClicked)
                },
                isLoading = state.isRegistering
            )
        }
    }

}


@Composable
fun PasswordRequiers(
    modifier: Modifier = Modifier,
    text: String,
    isValid: Boolean = false,

    ) {

    Row(
        modifier = modifier, verticalAlignment = Alignment.CenterVertically,
    ) {


        Icon(
            imageVector = if (isValid) CheckIcon else CrossIcon,
            contentDescription = null,
            tint = if (isValid) RuniqueGreen else Color.Red
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text,
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,

            )

    }


}

@OptIn(ExperimentalFoundationApi::class)

@Preview(showBackground = true)
@Composable
fun PreviewRegisterScreen() {

    RuniqueTheme {

        RegisterScreen(state = RegisterState())
    }
}
