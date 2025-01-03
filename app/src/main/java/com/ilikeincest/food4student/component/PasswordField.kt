package com.ilikeincest.food4student.component

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.ilikeincest.food4student.ui.theme.Food4StudentTheme

/**
 * A password field with show/hide button
 *
 * @param label Label of field
 * @param password Actual password inside field
 * @param onPasswordChange Callback invoked on password change
 * @param imeAction Specifies which button to use for keyboard's enter key
 * @param keyboardAction KeyboardAction invoked when user press enter key
 */
@Composable
fun PasswordField(
    label: String,
    password: String,
    onPasswordChange: (String) -> Unit,
    imeAction: ImeAction = ImeAction.Default,
    keyboardAction: KeyboardActions = KeyboardActions.Default,
    isError: Boolean = false,
    errorMessage: String = ""
) {
    var isPasswordVisible by remember { mutableStateOf(false) }

    val showPasswordIcon =
        if (isPasswordVisible) Icons.Default.VisibilityOff
        else Icons.Default.Visibility
    val showPasswordContentDesc =
        if (isPasswordVisible) "Hide password"
        else "Show password"

    TextField(
        value = password, onValueChange = onPasswordChange,
        singleLine = true,
        label = { Text(label) },
        trailingIcon = {
            IconButton(onClick = {
                isPasswordVisible = !isPasswordVisible
            }) {
                Icon(
                    imageVector = showPasswordIcon,
                    contentDescription = showPasswordContentDesc
                )
            }
        },
        isError = isError,
        supportingText = if (isError) {{ Text(errorMessage) }} else null,
        visualTransformation =
            if (isPasswordVisible) VisualTransformation.None
            else PasswordVisualTransformation(),
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Password,
            autoCorrectEnabled = false,
            imeAction = imeAction
        ),
        keyboardActions = keyboardAction,
        modifier = Modifier.fillMaxWidth()
    )
}

@PreviewLightDark
@Composable
private fun PasswordFieldPrev() {
    Food4StudentTheme {
        PasswordField("password label", "", {})
    }
}