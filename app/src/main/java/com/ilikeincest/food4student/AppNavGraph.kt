package com.ilikeincest.food4student

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.*
import androidx.navigation.toRoute
import com.ilikeincest.food4student.admin.screen.AdminScreen
import com.ilikeincest.food4student.model.Location
import com.ilikeincest.food4student.model.SavedShippingLocation
import com.ilikeincest.food4student.model.SavedShippingLocationType
import com.ilikeincest.food4student.screen.account_center.AccountCenterScreen
import com.ilikeincest.food4student.screen.auth.select_role.SelectRoleRestaurantScreen
import com.ilikeincest.food4student.screen.auth.select_role.SelectRoleScreen
import com.ilikeincest.food4student.screen.auth.select_role.SelectRoleUserScreen
import com.ilikeincest.food4student.screen.restaurant.RestaurantMainScreen
import com.ilikeincest.food4student.screen.auth.sign_in.SignInScreen
import com.ilikeincest.food4student.screen.auth.sign_up.SignUpScreen
import com.ilikeincest.food4student.screen.food_item.add_category.AddCategoryScreen
import com.ilikeincest.food4student.screen.food_item.add_edit_saved_product.AddEditSavedFoodItemScreen
import com.ilikeincest.food4student.screen.food_item.add_edit_saved_varations.AddEditSavedVariationScreen
import com.ilikeincest.food4student.screen.main_page.MainScreen
import com.ilikeincest.food4student.screen.restaurant.RestaurantViewModel
import com.ilikeincest.food4student.screen.shipping.add_edit_saved_location.AddEditSavedLocationScreen
import com.ilikeincest.food4student.screen.shipping.pick_location.MapScreen
import com.ilikeincest.food4student.screen.shipping.shipping_location.ShippingLocationScreen
import com.ilikeincest.food4student.screen.splash.SplashScreen
import com.ilikeincest.food4student.util.nav.*
import kotlinx.serialization.Serializable

object AppRoutes {
    @Serializable
    object Main

    // Auth
    @Serializable
    object SignIn
    @Serializable
    object SignUp
    @Serializable
    object Profile
    @Serializable
    object SelectRole
    @Serializable
    object SignUpAsUser
    @Serializable
    object SignUpAsRestaurant

    @Serializable
    object ShippingLocation
    @Serializable
    object PickLocation
    @Serializable
    object AddSavedLocation
    @Serializable
    data class EditSavedLocation(val id: String)

    @Serializable
    object Admin
    @Serializable
    object Restaurant
    @Serializable
    object AddEditFoodItem
    @Serializable
    object AddCategory
    @Serializable
    object AddEditVariation
    @Serializable
    object SplashScreen
}

@Composable
fun AppNavGraph(navController: NavHostController = rememberNavController()) {
    // to make the transition fade to/from this color, not from white always
    val bgColor = if (isSystemInDarkTheme()) {
        Color.Black
    } else {
        Color.White
    }
    NavHost(
        navController = navController,
        enterTransition = { slideInHorizontally(initialOffsetX = { fullWidth -> fullWidth }) + fadeIn() },
        popEnterTransition = { slideInHorizontally(initialOffsetX = { fullWidth -> -fullWidth }) + fadeIn() },
        exitTransition = { slideOutHorizontally(targetOffsetX = { fullWidth -> -fullWidth }) + fadeOut() },
        popExitTransition = { slideOutHorizontally(targetOffsetX = { fullWidth -> fullWidth }) + fadeOut() },
        startDestination = AppRoutes.SplashScreen,
        modifier = Modifier.background(color = bgColor)
    ) {
        composable<AppRoutes.SplashScreen> {
            SplashScreen(
                onSetRootAdmin = { navController.navigateAsRootRoute(AppRoutes.Admin) },
                onSetRootMain = { navController.navigateAsRootRoute(AppRoutes.Main) },
                onSetRootSignIn = { navController.navigateAsRootRoute(AppRoutes.SignIn) },
                onSetRootSelectRole = { navController.navigateAsRootRoute(AppRoutes.SelectRole) },
                onSetRootRestaurant = { navController.navigateAsRootRoute(AppRoutes.Restaurant) }
            )
        }
        composable<AppRoutes.Main> {
            MainScreen(
                onNavigateToAccountCenter = { navController.navigate(AppRoutes.Profile) },
                onNavigateToShippingLocation = {
                    navController.navigate(AppRoutes.ShippingLocation)
                },
                onNavigateToRestaurant = { /* TODO */ }
            )
        }
        composable<AppRoutes.Admin> {
            AdminScreen()
        }
        navigation(
            route = "RestaurantFlow",
            startDestination = "${AppRoutes.Restaurant}"
        ) {
            composable<AppRoutes.Restaurant> {
                val parentEntry = remember(navController) {
                    navController.getBackStackEntry("RestaurantFlow")
                }
                val sharedViewModel = hiltViewModel<RestaurantViewModel>(parentEntry)
                RestaurantMainScreen(
                    viewModel = sharedViewModel,
                    onNavigateToAddEditFoodItem = { navController.navigate(AppRoutes.AddEditFoodItem) },
                    navController = navController
                )
            }
            composable<AppRoutes.AddEditFoodItem> {
                val parentEntry = remember(navController) {
                    navController.getBackStackEntry("RestaurantFlow")
                }
                val sharedViewModel = hiltViewModel<RestaurantViewModel>(parentEntry)
                AddEditSavedFoodItemScreen(
                    viewModel = sharedViewModel,
                    onNavigateUp = { navController.navigateUp() },
                    onNavigateToFoodCategory = { navController.navigate(AppRoutes.AddCategory) },
                    onNavigateToVariation = { navController.navigate(AppRoutes.AddEditVariation) }
                )
            }
            composable<AppRoutes.AddCategory> {
                val parentEntry = remember(navController) {
                    navController.getBackStackEntry("RestaurantFlow")
                }
                val sharedViewModel = hiltViewModel<RestaurantViewModel>(parentEntry)
                AddCategoryScreen(
                    viewModel = sharedViewModel,
                    onNavigateUp = { navController.navigateUp() }
                )
            }
            composable<AppRoutes.AddEditVariation> {
                val parentEntry = remember(navController) {
                    navController.getBackStackEntry("RestaurantFlow")
                }
                val sharedViewModel = hiltViewModel<RestaurantViewModel>(parentEntry)
                AddEditSavedVariationScreen(
                    viewModel = sharedViewModel,
                    onNavigateUp = { navController.navigateUp() }
                )
            }
        }
        composable<AppRoutes.SignIn> {
            SignInScreen(
                onSetRootSplash = { navController.navigateAsRootRoute(AppRoutes.SplashScreen) },
                onNavigateToSignUp = { navController.navigate(AppRoutes.SignUp) }
            )
        }
        composable<AppRoutes.SignUp> {
            SignUpScreen(
                onSetRootSplash = { navController.navigateAsRootRoute(AppRoutes.SplashScreen) },
                onNavigateToSignIn = { navController.popBackStack(AppRoutes.SignIn, false) }
            )
        }
        composable<AppRoutes.SelectRole> {
            SelectRoleScreen(
                onSignUpAsUser = { navController.navigate(AppRoutes.SignUpAsUser) },
                onSignUpAsRestaurant = { navController.navigate(AppRoutes.SignUpAsRestaurant) }
            )
        }
        composable<AppRoutes.SignUpAsUser> {
            SelectRoleUserScreen(
                onSuccessSignUp = { navController.navigateAsRootRoute(AppRoutes.SplashScreen) }
            )
        }
        composable<AppRoutes.SignUpAsRestaurant> {
            NavigateWithResult(it) { location: Location? ->
                SelectRoleRestaurantScreen(
                    selectedLocation = location,
                    onNavigateToLocationPicker = { navController.navigate(AppRoutes.PickLocation) }
                )
            }
        }
        composable<AppRoutes.ShippingLocation> {
            ShippingLocationScreen(
                locationList = listOf(
                    SavedShippingLocation(
                        locationType = SavedShippingLocationType.Home,
                        buildingNote = "Cổng trước",
                        location = "KTX Đại học Quốc gia TPHCM - Khu B",
                        address = "15 Tô Vĩnh Diện, Phường Đông Hòa, Dĩ An, Bình Dương",
                        receiverName = "Hồ Nguyên Minh",
                        receiverPhone = "01234567879",
                    ),
                    SavedShippingLocation(
                        locationType = SavedShippingLocationType.Work,
                        buildingNote = "Cổng trước",
                        location = "KTX Đại học Quốc gia TPHCM - Khu B",
                        address = "15 Tô Vĩnh Diện, Phường Đông Hòa, Dĩ An, Bình Dương",
                        receiverName = "Hồ Nguyên Minh",
                        receiverPhone = "01234567879",
                    ),
                    SavedShippingLocation(
                        locationType = SavedShippingLocationType.Other,
                        otherLocationTypeTitle = "Dating location",
                        location = "Trường mẫu giáo Tư thục Sao Mai",
                        address = "Lmao u believe me fr?",
                        receiverName = "Hứa Văn Lý",
                        receiverPhone = "0123456789",
                    )
                ), // TODO: move to vm
                onNavigateUp = { navController.navigateUp() },
                onPickFromMap = { navController.navigate(AppRoutes.PickLocation) },
                onEditLocation = {
                    navController.navigate(AppRoutes.EditSavedLocation("")) // TODO
                }
            )
        }
        composable<AppRoutes.PickLocation> {
            MapScreen(
                onNavigateUp = { navController.navigateUp() },
                onSelectLocation = { navController.popBackWithResult(it) }
            )
        }
        composable<AppRoutes.AddSavedLocation> {
            AddEditSavedLocationScreen(onNavigateUp = {})
        }
        composable<AppRoutes.EditSavedLocation> {
            val route = it.toRoute<AppRoutes.EditSavedLocation>()
            AddEditSavedLocationScreen(
                onNavigateUp = { navController.navigateUp() },
                id = route.id
            )
        }
        composable<AppRoutes.Profile> {
            AccountCenterScreen(navController = navController)
        }
    }
}