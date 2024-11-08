package com.ilikeincest.food4student.screen.map

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.animateIntOffsetAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.here.sdk.core.GeoCoordinates
import com.ilikeincest.food4student.R
import com.ilikeincest.food4student.screen.map.component.MapSearchBar
import com.ilikeincest.food4student.screen.map.component.MapViewContainer
import com.ilikeincest.food4student.screen.map.component.SuggestedAddressList
import com.ilikeincest.food4student.util.LocationUtils
import com.ilikeincest.food4student.viewmodel.MapViewModel

@Composable
fun MapScreen(
    mapViewModel: MapViewModel = viewModel(),
) {
    //Request permission for location
    val context = LocalContext.current
    val locationUtils = remember { LocationUtils(context) }

    val requestPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
        onResult = { permissions ->
            locationUtils.handlePermissionResult(permissions, mapViewModel)
        }
    )

    LaunchedEffect(Unit) {
        if (!locationUtils.hasLocationPermission(context)) {
            locationUtils.requestLocationPermissions(requestPermissionLauncher)
        } else {
            locationUtils.requestLocationUpdates(mapViewModel)
        }
    }

    //The actual mapScreen
    val mapViewInitialized by mapViewModel.mapViewInitialized
    val nearbyPlaces by mapViewModel.nearbyPlaces.collectAsState()
    val location = mapViewModel.currentLocation.value

    // Focus on the location if available
    LaunchedEffect(location) {
        location?.let {
            val geoCoordinates = GeoCoordinates(it.latitude, it.longitude)
            mapViewModel.focusOnPlaceWithMarker(geoCoordinates)
        }
    }

    var expanded by remember { mutableStateOf(false) }
    val animatedOffset by animateIntOffsetAsState(
        targetValue = if (expanded) IntOffset(0, 100) else IntOffset(0, 0),
        label = "Search bar expanded content offset"
    )

    Scaffold { Box(Modifier.fillMaxSize()) {
        // Search bar on top
        if (mapViewInitialized) {
            MapSearchBar(
                onSearch = { query -> mapViewModel.autoSuggestExample(query) },
                searchResults = mapViewModel.searchResults,
                onResultClick = { place ->
                    mapViewModel.focusOnPlaceWithMarker(place)
                },
                onExpandedChange = { newValue ->
                    expanded = newValue
                },
                modifier = Modifier.align(Alignment.TopCenter)
            )
        }

        Column(Modifier.fillMaxSize().padding(it).absoluteOffset { animatedOffset }) {
            // Placeholder for the search bar
            Spacer(Modifier.height(68.dp))
            Box(
                modifier = Modifier
                    .weight(0.5f)
                    .fillMaxWidth()
            ) {
                MapViewContainer(
                    mapViewModel = mapViewModel
                )
                // point of interest icon
                Icon(
                    painterResource(R.drawable.poi),
                    null, tint = Color.Unspecified,
                    modifier = Modifier.align(Alignment.Center).size(40.dp)
                )
            }
            SuggestedAddressList(
                nearbyPlaces = nearbyPlaces,
                onPlaceClick = { place ->
                    place.geoCoordinates?.let { geoCoordinates ->
                        mapViewModel.focusOnPlaceWithMarker(geoCoordinates)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.5f)
            )
        }
    } }
}