package com.mohaberabi.auth.presentation.intro.screen

import android.media.tv.TvContract.Channels.Logo
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mohaberabi.auth.presentation.R
import com.mohaberabi.auth.presentation.intro.viewmodel.IntroAction
import com.mohaberabi.core.presentation.designsystem.compose.GradientBg
import com.mohaberabi.core.presentation.designsystem.compose.RuniqueButton
import com.mohaberabi.core.presentation.designsystem.compose.RuniqueOutlinedButton
import com.mohaberabi.core.presentation.designsystem.theme.LogoIcon
import com.mohaberabi.core.presentation.designsystem.theme.RuniqueTheme


/**
 * never use a reference of the viewmodel or controller
 * to be testable and also to make us of previews in the composable Preview
 */

@Composable

fun IntroScreenRoot(
    onSignIn: () -> Unit,
    onSignUp: () -> Unit,
) {

    IntroScreen(
        onAction = {

                action ->
            when (action) {
                IntroAction.OnSignInClick -> onSignIn()
                IntroAction.OnSignUpClick -> onSignUp()
            }
        },
    )
}


@Composable
fun IntroScreen(
    onAction: (IntroAction) -> Unit
) {

    GradientBg {

        Box(

            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            contentAlignment = Alignment.Center
        ) {


            AppLogoVertical()
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .padding(bottom = 48.dp)
        ) {
            Text(
                text = stringResource(R.string.welcome_to_runique),
                fontSize = 20.sp,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = stringResource(R.string.intro_description),
                style = MaterialTheme.typography.bodySmall,
            )

            Spacer(modifier = Modifier.height(32.dp))
            RuniqueOutlinedButton(
                label = stringResource(R.string.sign_in),
                onClick = { onAction(IntroAction.OnSignInClick) })

            Spacer(modifier = Modifier.height(16.dp))
            RuniqueButton(
                label = stringResource(R.string.sign_up),
                onClick = {
                    onAction(IntroAction.OnSignUpClick)
                },
            )
        }
    }
}

@Preview
@Composable
fun PreviewIntroScreen() {
    RuniqueTheme {

        IntroScreen(
            onAction = {},
        )
    }
}


@Composable
private fun AppLogoVertical() {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = LogoIcon,
            contentDescription = stringResource(id = R.string.runique),
            tint = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = stringResource(R.string.runique),
            fontSize = 24.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}