package com.ilikeincest.food4student.screen.auth.sign_in

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.ilikeincest.food4student.R
import com.ilikeincest.food4student.component.NormalField
import com.ilikeincest.food4student.component.PasswordField
import com.ilikeincest.food4student.component.ScreenPreview

@Composable
fun SignInScreen(modifier: Modifier = Modifier) {
    // big ass TODO: add event callbacks
    var email by remember { mutableStateOf("")}
    var password by remember { mutableStateOf("")}

    Column(
        verticalArrangement = Arrangement.Center,
        modifier = modifier
            .fillMaxSize()
            .padding(32.dp, 10.dp)
    ) {
        Text(
            text = "Đăng nhập",
            style = typography.displayMedium,
            modifier = Modifier.padding(bottom = 42.dp)
        )

        // TODO: add email validation
        // TODO: add wrong creds warning
        NormalField("email", email, { email = it }, KeyboardType.Email)
        Spacer(modifier = Modifier.height(24.dp))
        PasswordField("password", password, { password = it })
        Spacer(modifier = Modifier.height(4.dp))
        TextButton(onClick = {}, modifier = Modifier.align(Alignment.End)) {
            Text("quên mật khẩu?")
        }

        Spacer(modifier = Modifier.height(24.dp))

        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Button(onClick = {}, modifier = Modifier.fillMaxWidth()) {
                Text("Let's eat!")
            }
            OutlinedButton(onClick = {}, modifier = Modifier.fillMaxWidth()) {
                Icon(painterResource(R.drawable.google_g), null, tint = Color.Unspecified)
                Spacer(Modifier.width(8.dp))
                Text("Đăng nhập với Google")
            }
            OutlinedButton(onClick = {}, modifier = Modifier.fillMaxWidth()) {
                Text("Đăng ký?")
            }
        }
    }
}

@PreviewLightDark
@Composable
private fun SignInPrev() {
    ScreenPreview {
        SignInScreen()
    }
}