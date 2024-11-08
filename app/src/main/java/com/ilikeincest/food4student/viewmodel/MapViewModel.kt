package com.ilikeincest.food4student.viewmodel

import android.content.Context
import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import com.here.sdk.core.GeoCoordinates
import com.here.sdk.mapview.MapView
import com.here.sdk.search.Place
import com.ilikeincest.food4student.util.SearchExample
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class MapViewModel : ViewModel() {
    lateinit var searchExample: SearchExample

    var mapViewInitialized = mutableStateOf(false)

    private val _nearbyPlaces = MutableStateFlow<List<Place>>(emptyList())
    val nearbyPlaces: StateFlow<List<Place>> = _nearbyPlaces

    private val _searchResults = mutableStateListOf<Place>()
    val searchResults: SnapshotStateList<Place> = _searchResults

    private val _currentLocation = mutableStateOf<GeoCoordinates?>(null)
    val currentLocation: State<GeoCoordinates?> = _currentLocation

    fun setMapViewInitializedTrue() {
        mapViewInitialized.value = true
    }

    fun setNearbyPlaces(places: List<Place>) {
        _nearbyPlaces.value = places
    }

    fun initializeSearchExample(context: Context, mapView: MapView) {
        searchExample = SearchExample(context, mapView).apply {
            onNearbyPlacesFetched = { places ->
                _nearbyPlaces.value = places
            }
        }
    }

    fun autoSuggestExample(query: String) {
        if (!::searchExample.isInitialized) {
            throw UninitializedPropertyAccessException("SearchExample has not been initialized")
        }
        _searchResults.clear()
        Log.d("MapViewModel", "Query: $query")
        _searchResults.addAll(searchExample.autoSuggestExample(query))
    }

    fun focusOnPlaceWithMarker(geoCoordinates: GeoCoordinates) {
        if (!::searchExample.isInitialized) {
            throw UninitializedPropertyAccessException("SearchExample has not been initialized")
        }
        searchExample.focusOnPlaceWithMarker(geoCoordinates)
    }

    fun updateCurrentLocation(newLocation: GeoCoordinates){
        _currentLocation.value = newLocation
    }
}