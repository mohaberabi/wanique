import android.media.Image
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text2.BasicSecureTextField
import androidx.compose.foundation.text2.BasicTextField2
import androidx.compose.foundation.text2.input.TextFieldState
import androidx.compose.foundation.text2.input.TextObfuscationMode
import androidx.compose.foundation.text2.input.rememberTextFieldState
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mohaberabi.core.presentation.designsystem.theme.CheckIcon
import com.mohaberabi.core.presentation.designsystem.theme.EmailIcon
import com.mohaberabi.core.presentation.designsystem.theme.EyeClosedIcon
import com.mohaberabi.core.presentation.designsystem.theme.EyeOpenedIcon
import com.mohaberabi.core.presentation.designsystem.theme.LockIcon
import com.mohaberabi.core.presentation.designsystem.theme.RuniqueTheme


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun RuniquePasswordTextField(
    modifier: Modifier = Modifier,
    state: TextFieldState,
    hint: String = "",
    title: String? = null,
    isPassword: Boolean = true,
    onTogglePassword: () -> Unit = {},
) {

    val colors = MaterialTheme.colorScheme
    var isFocused by remember {

        mutableStateOf(false)
    }


    Column(
        modifier = modifier,
    ) {

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {

            if (title != null) {
                Text(
                    text = title,
                    color = colors.onSurfaceVariant,
                )
            }

        }

        Spacer(modifier = Modifier.height(4.dp))

        BasicSecureTextField(
            keyboardType = KeyboardType.Password,
            state = state,
            textObfuscationMode = if (isPassword) TextObfuscationMode.Hidden else TextObfuscationMode.Visible,
            textStyle = LocalTextStyle.current.copy(
                color = colors.onBackground,
            ),
            cursorBrush = SolidColor(colors.onBackground),
            modifier = Modifier
                .clip(RoundedCornerShape(16.dp))
                .background(if (isFocused) colors.primary.copy(alpha = 0.05f) else colors.surface)
                .border(
                    width = 1.dp,
                    color = if (isFocused) colors.primary else Color.Transparent,
                    shape = RoundedCornerShape(16.dp)
                )
                .padding(12.dp)
                .onFocusChanged {
                    isFocused = it.isFocused
                },
            decorator = {

                    innerBox ->


                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {


                    Icon(
                        imageVector = LockIcon,
                        contentDescription = null,
                        tint = colors.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Box(modifier = Modifier.weight(1f)) {
                        if (state.text.isEmpty() && !isFocused) {
                            Text(text = hint, color = colors.onSurfaceVariant.copy(alpha = 0.4f))
                        }

                        innerBox()
                    }
                    IconButton(onClick = onTogglePassword) {
                        Icon(
                            imageVector = if (isPassword) EyeClosedIcon else EyeOpenedIcon,
                            contentDescription = null,
                            tint = colors.onSurfaceVariant,
                        )
                    }
                }

            }
        )

    }
}

@OptIn(ExperimentalFoundationApi::class)
@Preview(showBackground = false)
@Composable

fun PreviewRuniqueTextField() {
    RuniqueTheme {

        RuniquePasswordTextField(
            state = rememberTextFieldState(),
            title = "Email",
            hint = "example@test.com",
            modifier = Modifier.fillMaxWidth()
        )
    }
}