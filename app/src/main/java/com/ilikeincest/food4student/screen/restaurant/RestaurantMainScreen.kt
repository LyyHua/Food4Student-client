package com.ilikeincest.food4student.screen.restaurant

import android.annotation.SuppressLint
import android.os.Build
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Checklist
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.ilikeincest.food4student.screen.main_page.RequestNotificationPermissionDialog
import com.ilikeincest.food4student.screen.main_page.notification.NotificationScreenViewModel

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RestaurantMainScreen(
    viewModel: RestaurantViewModel,
    onNavigateToAddEditFoodItem: () -> Unit,
    navController: NavController,
) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        RequestNotificationPermissionDialog()
    }

    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    var currentRoute by rememberSaveable { mutableStateOf(defaultRoute) }
    Scaffold(
        bottomBar = { NavigationBar {
            for (route in MainRestaurantRoutes.entries) {
                val isSelected = route == currentRoute
                val iconRes = if (isSelected) route.selectedIcon
                    else route.unselectedIcon
                val icon = @Composable {
                    Icon(iconRes, null)
                }
                NavigationBarItem(
                    selected = isSelected,
                    onClick = {
                        if (currentRoute != route)
                            currentRoute = route
                    },
                    label = { Text(stringResource(route.labelResId)) },
                    icon = icon
                )
            }
        } },
        // show top bar if route is notification and orders (why the heck don't we give these 2 screen a freaking topAppBar you ADHD piece of *
        topBar = {
            val topBarAlpha by animateFloatAsState(
                targetValue = if (currentRoute == MainRestaurantRoutes.Notifications || currentRoute == MainRestaurantRoutes.Orders) 1f else 0f,
                label = "Top bar alpha"
            )
            val topBarHeight by animateDpAsState(
                targetValue = if (currentRoute == MainRestaurantRoutes.Notifications || currentRoute == MainRestaurantRoutes.Orders)
                    TopAppBarDefaults.TopAppBarExpandedHeight else 0.dp,
                label = "Top bar height"
            )
            TopAppBar(
                title = {
                    if (currentRoute == MainRestaurantRoutes.Orders) {
                        Text("Orders")
                    } else {
                        Text("Notification")
                    }
                },
                modifier = Modifier.alpha(topBarAlpha),
                actions = {
                    if (currentRoute == MainRestaurantRoutes.Notifications) {
                        val notiVm: NotificationScreenViewModel = hiltViewModel()
                        IconButton(onClick = { notiVm.markAllAsRead() }) {
                            Icon(Icons.Filled.Checklist, "Mark all as read")
                        }
                    }
                },
                scrollBehavior = scrollBehavior,
                expandedHeight = topBarHeight,
            )
        },
    ) { innerPadding ->
        val finalPadding = if (currentRoute == MainRestaurantRoutes.Notifications) {
            Modifier.padding(innerPadding)
        } else {
            Modifier.fillMaxSize()
        }
        Box (modifier = finalPadding) {
            RestaurantPageNavGraph(
                currentRoute = currentRoute,
                scrollConnection = scrollBehavior.nestedScrollConnection,
                onNavigateToAddEditFoodItem = { onNavigateToAddEditFoodItem() },
                navController = navController,
                viewModel = viewModel
            )
        }
    }
}