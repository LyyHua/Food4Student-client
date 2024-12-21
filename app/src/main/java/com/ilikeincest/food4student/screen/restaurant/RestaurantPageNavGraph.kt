package com.ilikeincest.food4student.screen.restaurant

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.togetherWith
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.hilt.navigation.compose.hiltViewModel
import com.ilikeincest.food4student.component.BroadcastReceiver
import com.ilikeincest.food4student.model.Notification
import com.ilikeincest.food4student.screen.account_center.AccountCenterScreen
import com.ilikeincest.food4student.screen.main_page.notification.NotificationScreen
import com.ilikeincest.food4student.screen.main_page.notification.NotificationScreenViewModel
import com.ilikeincest.food4student.screen.main_page.order.OrderScreen
import kotlinx.datetime.Clock
import kotlin.random.Random

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.automirrored.outlined.ReceiptLong
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.Fastfood
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Person
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavController
import com.ilikeincest.food4student.R

internal val defaultRoute = MainRestaurantRoutes.Home

internal enum class MainRestaurantRoutes(
    @StringRes val labelResId: Int,
    val unselectedIcon: ImageVector,
    val selectedIcon: ImageVector
) {
    Home(
        R.string.home_screen_label,
        Icons.Outlined.Fastfood,
        Icons.Filled.Fastfood
    ),
    Orders(
        R.string.order_screen_label,
        Icons.AutoMirrored.Outlined.ReceiptLong,
        Icons.AutoMirrored.Filled.ReceiptLong
    ),
    Notifications(
        R.string.notification_screen_label,
        Icons.Outlined.Notifications,
        Icons.Filled.Notifications
    ),
    Account(
        R.string.account_center_label,
        Icons.Outlined.Person,
        Icons.Filled.Person
    ),
}

@Composable
internal fun RestaurantPageNavGraph(
    currentRoute: MainRestaurantRoutes,
    scrollConnection: NestedScrollConnection,
    onNavigateToAddEditFoodItem: () -> Unit,
    modifier: Modifier = Modifier,
    navController: NavController,
    viewModel: RestaurantViewModel
) {
    val notificationViewModel = hiltViewModel<NotificationScreenViewModel>()
    BroadcastReceiver("com.ilikeincest.food4student.NEW_MESSAGE") {
        val title = it?.getStringExtra("title")
        val message = it?.getStringExtra("message")
        val imageUrl = it?.getStringExtra("imageUrl")

        if (message == null) throw Error("Message cant be null")

        val newNoti = Notification(
            id = Random.nextInt().toString(),
            image = imageUrl,
            title = title ?: "Thông báo",
            timestamp = Clock.System.now(),
            content = message,
            isUnread = true
        )
        notificationViewModel.addNewNotification(newNoti)
    }

    val inTransition = fadeIn(tween(durationMillis = 250)) + slideInVertically { it / 50 }
    val outTransition = fadeOut(tween(durationMillis = 250))
    AnimatedContent(
        targetState = currentRoute,
        modifier = modifier,
        transitionSpec = {
            inTransition togetherWith outTransition using SizeTransform()
        },
        label = "Main screen swap page"
    ) {
        when (it) {
            MainRestaurantRoutes.Home ->
                RestaurantScreenContent(
                    onNavigateToAddEditFoodItem = {
                        onNavigateToAddEditFoodItem()
                    },
                    viewModel = viewModel
                )
            MainRestaurantRoutes.Orders ->
                OrderScreen()
            MainRestaurantRoutes.Account ->
                AccountCenterScreen(navController = navController)
            MainRestaurantRoutes.Notifications ->
                NotificationScreen(scrollConnection)
        }
    }
}