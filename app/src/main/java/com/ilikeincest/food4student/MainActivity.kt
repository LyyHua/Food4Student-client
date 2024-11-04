package com.ilikeincest.food4student

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ilikeincest.food4student.initializer.HereInitializer
import com.ilikeincest.food4student.screen.map.MapScreen
import com.ilikeincest.food4student.ui.theme.Food4StudentTheme
import com.ilikeincest.food4student.viewmodel.LocationViewModel
import com.ilikeincest.food4student.viewmodel.MapViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlin.getValue

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val mapViewModel: MapViewModel by viewModels()
    private val locationViewModel: LocationViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize HERE SDK
        val hereInitializer = HereInitializer(this)
        hereInitializer.initializeHERESDK()

        enableEdgeToEdge()
        setContent {
            Food4StudentTheme {
//                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
//                    OrderCard(
//                        id = "5ea765ds",
//                        date = LocalDate.of(1969, 2, 28),
//                        shopName = "Phúc Long",
//                        shopId = "fuck u",
//                        shopImage = { MonogramAvatar(initials = "PL", it) },
//                        orderItems = listOf(
//                            OrderItem("Trà sữa Phô mai tươi", "Size S - không đá", 2, 54_000),
//                            OrderItem("Trà sữa Phô mai tươi 2", "Size S - không đá", 2, 54_000),
//                            OrderItem("Trà sữa Phô mai tươi 3", "Size S - không đá", 2, 54_000),
//                        ),
//                        modifier = Modifier.fillMaxWidth().padding(innerPadding)
//                    )
                MapScreen(mapViewModel = mapViewModel, locationViewModel = locationViewModel)
//                }
            }
        }
    }
}

@Composable
fun TestLogInWithGoogle(
    modifier: Modifier = Modifier,
    appViewModel: AppViewModel = viewModel()
) {
    Column(modifier = modifier) {
        AuthenticationButton(
            "Login bitch",
            onGetCredentialResponse = { credential ->
                appViewModel.onSignInWithGoogle(credential)
            }
        )
    }
}