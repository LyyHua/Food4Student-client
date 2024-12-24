package com.ilikeincest.food4student.screen.main_page.home

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.SecondaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.here.sdk.core.GeoCoordinates
import com.ilikeincest.food4student.component.BetterPullToRefreshBox
import com.ilikeincest.food4student.component.ErrorDialog
import com.ilikeincest.food4student.dto.NoNeedToFetchAgainBuddy
import com.ilikeincest.food4student.screen.main_page.component.ShopListingCard
import com.ilikeincest.food4student.screen.main_page.home.component.AdsBanner
import com.ilikeincest.food4student.screen.main_page.home.component.CurrentShippingLocationCard
import com.ilikeincest.food4student.util.LocationUtils
import kotlinx.coroutines.launch

enum class HomeTabTypes(val tabTitle: String) {
    Nearby("Gần đây"),
    BestSeller("Bán chạy"),
    Loved("Được yêu thích")
}

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToShippingLocation: () -> Unit,
    onNavigateToRestaurant: (noNeedToFetchAgainBuddy: NoNeedToFetchAgainBuddy) -> Unit,
    currentLocation: GeoCoordinates?,
    vm: HomeViewModel = hiltViewModel()
) {
    val errorMessage by vm.errorMessage.collectAsState()
    val isLoadingMore by vm.isLoadingMore.collectAsState()
    val isRefreshing by vm.isRefreshing.collectAsState()
    if (errorMessage.isNotEmpty()) {
        ErrorDialog(
            message = errorMessage,
            onDismiss = { vm.dismissError() },
        )
    }

    Column(Modifier.padding(top = 12.dp)) {
        CurrentShippingLocationCard(
            onClick = onNavigateToShippingLocation,
            currentLocation = "24 Lý Thường Kiệt, Quận 69, Tp. Thủ Đức", // TODO
            modifier = Modifier
                .padding(bottom = 12.dp)
                .padding(horizontal = 16.dp)
        )

        val restaurantList = vm.restaurantList
        val selectedTab by vm.selectedTab.collectAsState()
        val noMoreRestaurant by vm.noMoreRestaurant.collectAsState()
        val state = rememberLazyListState()

        // on select another tab
        LaunchedEffect(selectedTab, currentLocation) {
            if (state.firstVisibleItemIndex > 0)
                state.animateScrollToItem(1)
            if (currentLocation != null)
                vm.refreshRestaurantList(currentLocation.latitude, currentLocation.longitude)
        }

        val coroutineScope = rememberCoroutineScope()
        BetterPullToRefreshBox(
            lazyListState = state,
            isRefreshing = isRefreshing,
            onRefresh = {
                if (currentLocation != null) {
                    coroutineScope.launch {
                        vm.refreshRestaurantList(currentLocation.latitude, currentLocation.longitude)
                    }
                }
            },
            modifier = Modifier.fillMaxHeight()
        ) {
            LazyColumn(state = state, modifier = Modifier.fillMaxHeight()) {
                item {
                    AdsBanner(
                        Modifier
                            .padding(horizontal = 16.dp)
                            .padding(bottom = 12.dp)
                    )
                }

                item {} // To have something to scroll up
                stickyHeader {
                    SecondaryTabRow(
                        selectedTabIndex = HomeTabTypes.entries.indexOf(selectedTab)
                    ) {
                        HomeTabTypes.entries.forEach {
                            Tab(
                                selected = selectedTab == it,
                                onClick = { vm.selectTab(it) },
                                text = { Text(it.tabTitle) }
                            )
                        }
                    }
                }

                items(restaurantList, key = { it.id }) { restaurant ->
                    Column {
                        val noNeedToFetchAgainBuddy = NoNeedToFetchAgainBuddy(
                            Id = restaurant.id,
                            TimeAway = restaurant.estimatedTimeInMinutes,
                            Distance = restaurant.distanceInKm,
                            IsFavorited = restaurant.isFavorited
                        )
                        ShopListingCard(
                            shopName = restaurant.name,
                            starRating = restaurant.averageRating.toString(),
                            distance = "${String.format("%.2f", restaurant.distanceInKm)} km",
                            timeAway = "${restaurant.estimatedTimeInMinutes} phút",
                            shopImageModel = restaurant.logoUrl, // TODO
                            isFavorite = restaurant.isFavorited, // disable favorite on home page
                            onFavoriteChange = { vm.toggleLike(restaurant.id) },
                            onClick = { onNavigateToRestaurant(noNeedToFetchAgainBuddy) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        )
                        HorizontalDivider()
                    }
                }
                item {
                    LaunchedEffect(isLoadingMore, noMoreRestaurant, isRefreshing) {
                        if (isLoadingMore || noMoreRestaurant || isRefreshing)
                            return@LaunchedEffect
                        currentLocation?.let {
                            vm.loadMoreRestaurants(currentLocation!!)
                        }
                    }
                    if (noMoreRestaurant) {
                        Text(
                            "Hết nhà hàng rồi bạn ơi (┬┬﹏┬┬)",
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        )
                    } else {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            CircularProgressIndicator(
                                Modifier
                                    .padding(16.dp)
                                    .size(32.dp),
                            )
                            Text("Đang tải thêm nhà hàng (*/ω＼*)")
                        }
                    }
                }
            }
        }
    }
}