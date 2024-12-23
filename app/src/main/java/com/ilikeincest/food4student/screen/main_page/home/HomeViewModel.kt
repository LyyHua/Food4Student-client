package com.ilikeincest.food4student.screen.main_page.home

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.here.sdk.core.GeoCoordinates
import com.ilikeincest.food4student.model.Restaurant
import com.ilikeincest.food4student.service.api.RestaurantApiService
import com.ilikeincest.food4student.service.api.UserApiService
import com.ilikeincest.food4student.util.haversineDistance
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val pageSize = 10

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val restaurantApi: RestaurantApiService,
    private val userApiService: UserApiService
) : ViewModel() {
    // the list that's shown on screen
    val restaurantList = mutableStateListOf<Restaurant>()

    private val _errorMessage = MutableStateFlow("")
    val errorMessage = _errorMessage.asStateFlow()

    private val _isLoadingMore = MutableStateFlow(false)
    val isLoadingMore = _isLoadingMore.asStateFlow()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing = _isRefreshing.asStateFlow()

    private val _noMoreRestaurant = MutableStateFlow(false)
    val noMoreRestaurant = _noMoreRestaurant.asStateFlow()

    private val _selectedTab = MutableStateFlow(HomeTabTypes.Nearby)
    val selectedTab = _selectedTab.asStateFlow()

    private var _currentPage = 1

    private val _currentLocation = MutableStateFlow<GeoCoordinates?>(null)
    val currentLocation: StateFlow<GeoCoordinates?> = _currentLocation.asStateFlow()

    fun updateCurrentLocation(location: GeoCoordinates) {
        _currentLocation.value = location
        viewModelScope.launch {
            refreshRestaurantList()
        }
    }

    fun selectTab(i: HomeTabTypes) { _selectedTab.value = i }

    suspend fun refreshRestaurantList() {
        val location = _currentLocation.value
        if (location == null) {
            _errorMessage.value = "Location not available. Please enable location permissions."
            _isRefreshing.value = false
            return
        }
        _isRefreshing.value = true
        _isLoadingMore.value = false
        _currentPage = 1
        val res = restaurantApi.getRestaurants(location.latitude, location.longitude, 1, pageSize)
        if (!res.isSuccessful) {
            _errorMessage.value = "Không thể load danh sách nhà hàng.\n" +
                    "Mã lỗi: ${res.code()}${res.message()}"
            _isRefreshing.value = false
            return
        }
        val favorites = res.body()?.associateBy { it.id }

        restaurantList.clear()
        res.body()?.forEach { dto ->
            val distance = haversineDistance(
                lat1 = location.latitude,
                lon1 = location.longitude,
                lat2 = dto.latitude,
                lon2 = dto.longitude
            )
            val estimatedTime = (distance / 40.0 * 60).toInt() // 40 km/h => time in minutes

            restaurantList.add(
                Restaurant(
                    id = dto.id,
                    isApproved = dto.isApproved,
                    name = dto.name,
                    description = dto.description,
                    address = dto.address,
                    latitude = dto.latitude,
                    longitude = dto.longitude,
                    logoUrl = dto.logoUrl,
                    bannerUrl = dto.bannerUrl,
                    totalRatings = dto.totalRatings,
                    averageRating = dto.averageRating,
                    isLiked = dto.isLiked,
                    foodCategories = emptyList(), // Populate as needed
                    distanceInKm = distance,
                    estimatedTimeInMinutes = estimatedTime
                )
            )
        }
        _noMoreRestaurant.value = false
        _isRefreshing.value = false
    }

    fun loadMoreRestaurants(currentLocation: GeoCoordinates) {
        _isLoadingMore.value = true
        _currentPage++
        viewModelScope.launch {
            val res = restaurantApi.getRestaurants(currentLocation.latitude, currentLocation.longitude, _currentPage, pageSize)

            if (!res.isSuccessful) {
                _errorMessage.value = "Không thể load danh sách nhà hàng.\n" +
                        "Mã lỗi: ${res.code()} ${res.message()}"
                _isLoadingMore.value = false
                return@launch
            }

            val favorites = res.body()?.associateBy { it.id }

            val newList = res.body()?.map { dto ->
                val distance = haversineDistance(
                    lat1 = currentLocation.latitude,
                    lon1 = currentLocation.longitude,
                    lat2 = dto.latitude,
                    lon2 = dto.longitude
                )
                val estimatedTime = (distance / 40.0 * 60).toInt() // 40 km/h => time in minutes

                Restaurant(
                    id = dto.id,
                    isApproved = dto.isApproved,
                    name = dto.name,
                    description = dto.description,
                    address = dto.address,
                    latitude = dto.latitude,
                    longitude = dto.longitude,
                    logoUrl = dto.logoUrl,
                    bannerUrl = dto.bannerUrl,
                    totalRatings = dto.totalRatings,
                    averageRating = dto.averageRating,
                    isLiked = dto.isLiked,
                    foodCategories = emptyList(), // Populate as needed
                    distanceInKm = distance,
                    estimatedTimeInMinutes = estimatedTime
                )
            } ?: listOf()

            restaurantList.addAll(newList)

            if (newList.size < pageSize) {
                _noMoreRestaurant.value = true
            }

            _isLoadingMore.value = false
        }
    }

    fun toggleLike(restaurantId: String) {
        viewModelScope.launch {
            userApiService.toggleLikeRestaurant(restaurantId)
            val index = restaurantList.indexOfFirst { it.id == restaurantId }
            val newRes = restaurantList[index].copy(
                isLiked = !restaurantList[index].isLiked
            )
            restaurantList[index] = newRes
        }
    }

    fun dismissError() {
        _errorMessage.value = ""
    }
}