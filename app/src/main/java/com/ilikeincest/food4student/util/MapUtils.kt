package com.ilikeincest.food4student.util

import android.app.AlertDialog
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.mutableStateListOf
import com.here.sdk.core.Anchor2D
import com.here.sdk.core.CustomMetadataValue
import com.here.sdk.core.GeoBox
import com.here.sdk.core.GeoCoordinates
import com.here.sdk.core.LanguageCode
import com.here.sdk.core.Metadata
import com.here.sdk.core.Point2D
import com.here.sdk.core.Rectangle2D
import com.here.sdk.core.Size2D
import com.here.sdk.mapview.MapImageFactory
import com.here.sdk.mapview.MapMarker
import com.here.sdk.mapview.MapMeasure
import com.here.sdk.mapview.MapScene.MapPickFilter
import com.here.sdk.mapview.MapView
import com.here.sdk.mapview.MapViewBase.MapPickCallback
import com.here.sdk.search.AddressQuery
import com.here.sdk.search.Place
import com.here.sdk.search.SearchCallback
import com.here.sdk.search.SearchEngine
import com.here.sdk.search.SearchError
import com.here.sdk.search.SearchOptions
import com.here.sdk.search.SuggestCallback
import com.here.sdk.search.TextQuery
import com.ilikeincest.food4student.R

// TODO: clean up this fucking mess
//class MapUtils {}
class SearchExample(private val context: Context, private val mapView: MapView) {
    val camera = mapView.camera
    private val mapMarkerList: MutableList<MapMarker> = ArrayList()
    private var searchEngine: SearchEngine? = null
    val searchResults = mutableStateListOf<Place>()
    var onNearbyPlacesFetched: ((List<Place>) -> Unit)? = null
    var centerMarker: MapMarker? = null
    private val handler = Handler(Looper.getMainLooper())
    private var isDragging = false

    private fun geocodeAnAddress() {
        // Set map to expected location.
        val geoCoordinates = GeoCoordinates(52.53086, 13.38469)
        val distanceInMeters = (1000 * 7).toDouble()
        val mapMeasureZoom = MapMeasure(MapMeasure.Kind.DISTANCE, distanceInMeters)
        camera.lookAt(geoCoordinates, mapMeasureZoom)

        val queryString = "Invalidenstraße 116, Berlin"

        Toast.makeText(
            context,
            ("Finding locations for: " + queryString
                    + ". Tap marker to see the coordinates. Check the logs for the address."),
            Toast.LENGTH_LONG
        ).show()

        geocodeAddressAtLocation(queryString, geoCoordinates)
    }

    fun getAddressForCoordinates(geoCoordinates: GeoCoordinates) {
        val reverseGeocodingOptions = SearchOptions()
        reverseGeocodingOptions.languageCode = LanguageCode.EN_GB
        reverseGeocodingOptions.maxItems = 5

        searchEngine!!.search(geoCoordinates, reverseGeocodingOptions, addressSearchCallback)
    }

    private val addressSearchCallback = SearchCallback { searchError, list ->
        if (searchError == SearchError.OPERATION_CANCELLED) {
            return@SearchCallback
        }
        if (searchError != null) {
            showDialog("Reverse geocoding", "Error: $searchError")
            return@SearchCallback
        }
        // If error is null, list is guaranteed to be not empty.
        onNearbyPlacesFetched?.invoke(list!!.mapNotNull { it })
    }

    private fun pickMapMarker(touchPOint: Point2D) {
        val originInPixels = Point2D(touchPOint.x, touchPOint.y)
        val sizeInPixels = Size2D(1.0, 1.0)
        val rectangle = Rectangle2D(originInPixels, sizeInPixels)

        // Creates a list of map content type from which the results will be picked.
        // The content type values can be MAP_CONTENT, MAP_ITEMS and CUSTOM_LAYER_DATA.
        val contentTypesToPickFrom = ArrayList<MapPickFilter.ContentType>()

        // MAP_CONTENT is used when picking embedded carto POIs, traffic incidents, vehicle restriction etc.
        // MAP_ITEMS is used when picking map items such as MapMarker, MapPolyline, MapPolygon etc.
        // Currently we need map markers so adding the MAP_ITEMS filter.
        contentTypesToPickFrom.add(MapPickFilter.ContentType.MAP_ITEMS)
        val filter = MapPickFilter(contentTypesToPickFrom)

        // If you do not want to specify any filter you can pass filter as NULL and all of the pickable contents will be picked.
        mapView.pick(filter, rectangle,
            MapPickCallback { mapPickResult ->
                if (mapPickResult == null) {
                    // An error occurred while performing the pick operation.
                    return@MapPickCallback
                }
                val mapMarkerList = mapPickResult.mapItems!!
                    .markers
                if (mapMarkerList.isEmpty()) {
                    return@MapPickCallback
                }
                val topmostMapMarker = mapMarkerList[0]
                val metadata = topmostMapMarker.metadata
                if (metadata != null) {
                    val customMetadataValue = metadata.getCustomValue("key_search_result")
                    if (customMetadataValue != null) {
                        val searchResultMetadata = customMetadataValue as SearchResultMetadata
                        val title = searchResultMetadata.searchResult.title
                        val vicinity = searchResultMetadata.searchResult.address.addressText
                        showDialog("Picked Search Result", "$title. Vicinity: $vicinity")
                        return@MapPickCallback
                    }
                }
                showDialog(
                    "Picked Map Marker",
                    "Geographic coordinates: " +
                            topmostMapMarker.coordinates.latitude + ", " +
                            topmostMapMarker.coordinates.longitude
                )
            })
    }

    fun searchInViewport(queryString: String) {
        clearMap()

        val viewportGeoBox = mapViewGeoBox
        val queryArea = TextQuery.Area(viewportGeoBox)
        val query = TextQuery(queryString, queryArea)

        val searchOptions = SearchOptions()
        searchOptions.languageCode = LanguageCode.EN_US
        searchOptions.maxItems = 30

        searchEngine!!.search(query, searchOptions, querySearchCallback)
    }

    private val querySearchCallback =
        SearchCallback { searchError, list ->
            if (searchError != null) {
                showDialog("Search", "Error: $searchError")
                return@SearchCallback
            }
            // If error is null, list is guaranteed to be not empty.
            showDialog("Search", "Results: " + list!!.size)

            // Add new marker for each search result on map.
            for (searchResult in list) {
                val metadata = Metadata()
                metadata.setCustomValue("key_search_result", SearchResultMetadata(searchResult))
                // Note: getGeoCoordinates() may return null only for Suggestions.
                addPoiMapMarker(searchResult.geoCoordinates, metadata)
            }
        }

    private class SearchResultMetadata(val searchResult: Place) : CustomMetadataValue {
        override fun getTag(): String {
            return "SearchResult Metadata"
        }
    }

    private val autosuggestCallback = SuggestCallback { searchError, list ->
        if (searchError != null) {
            Log.d(LOG_TAG, "Autosuggest Error: " + searchError.name)
            return@SuggestCallback
        }
        // If error is null, list is guaranteed to be not empty.
        Log.d(LOG_TAG, "Autosuggest results: " + list!!.size)
        searchResults.clear()
        searchResults.addAll(list.mapNotNull { it.place })
    }

    fun autoSuggestExample(query: String) : List<Place> {
        val centerGeoCoordinates = mapView.camera.state.targetCoordinates

        val searchOptions = SearchOptions()
        searchOptions.languageCode = LanguageCode.EN_US
        searchOptions.maxItems = 20

        val queryArea = TextQuery.Area(centerGeoCoordinates)

        searchEngine!!.suggest(
            TextQuery(query, queryArea),
            searchOptions,
            autosuggestCallback
        )
        return searchResults
    }

    private fun geocodeAddressAtLocation(queryString: String, geoCoordinates: GeoCoordinates) {
        clearMap()

        val query = AddressQuery(queryString, geoCoordinates)

        val options = SearchOptions()
        options.languageCode = LanguageCode.DE_DE
        options.maxItems = 30

        searchEngine!!.search(query, options, geocodeAddressSearchCallback)
    }

    private val geocodeAddressSearchCallback =
        SearchCallback { searchError, list ->
            if (searchError != null) {
                showDialog("Geocoding", "Error: $searchError")
                return@SearchCallback
            }
            for (geocodingResult in list!!) {
                // Note: getGeoCoordinates() may return null only for Suggestions.
                val geoCoordinates = geocodingResult.geoCoordinates
                val address = geocodingResult.address
                val locationDetails = (address.addressText
                        + ". GeoCoordinates: " + geoCoordinates!!.latitude
                        + ", " + geoCoordinates.longitude)

                Log.d(
                    LOG_TAG,
                    "GeocodingResult: $locationDetails"
                )
                addPoiMapMarker(geoCoordinates)
            }
            showDialog("Geocoding result", "Size: " + list.size)
        }

    init {
        try {
            searchEngine = SearchEngine()
        } catch (e: Exception) {
            throw RuntimeException("Initialization of SearchEngine failed: ${e.message}")
        }

        // Listen for camera movements
        mapView.camera.addListener {
            updateCenterMarkerPosition()
            if (!isDragging) {
                isDragging = true
                handler.postDelayed({
                    isDragging = false
                    getAddressForCoordinates(camera.state.targetCoordinates)
                }, 1500)
            }
        }
    }

    private fun updateCenterMarkerPosition() {
        centerMarker?.coordinates = camera.state.targetCoordinates
    }

    fun addPoiMapMarker(geoCoordinates: GeoCoordinates?) {
        val mapMarker = createPoiMapMarker(geoCoordinates)
        mapView.mapScene.addMapMarker(mapMarker)
        mapMarkerList.add(mapMarker)
    }

    private fun addPoiMapMarker(geoCoordinates: GeoCoordinates?, metadata: Metadata) {
        val mapMarker = createPoiMapMarker(geoCoordinates)
        mapMarker.metadata = metadata
        mapView.mapScene.addMapMarker(mapMarker)
        mapMarkerList.add(mapMarker)
    }

    private fun createPoiMapMarker(geoCoordinates: GeoCoordinates?): MapMarker {
        val mapImage = MapImageFactory.fromResource(context.resources, R.drawable.poi)
        return MapMarker(geoCoordinates!!, mapImage, Anchor2D(0.5, 1.0))
    }

    private val mapViewCenter: GeoCoordinates
        get() = mapView.camera.state.targetCoordinates

    private val mapViewGeoBox: GeoBox
        get() {
            val mapViewWidthInPixels = mapView.width
            val mapViewHeightInPixels = mapView.height
            val bottomLeftPoint2D = Point2D(0.0, mapViewHeightInPixels.toDouble())
            val topRightPoint2D = Point2D(mapViewWidthInPixels.toDouble(), 0.0)

            val southWestCorner = mapView.viewToGeoCoordinates(bottomLeftPoint2D)
            val northEastCorner = mapView.viewToGeoCoordinates(topRightPoint2D)

            if (southWestCorner == null || northEastCorner == null) {
                throw RuntimeException("GeoBox creation failed, corners are null.")
            }

            // Note: This algorithm assumes an unrotated map view.
            return GeoBox(southWestCorner, northEastCorner)
        }

    private fun clearMap() {
        for (mapMarker in mapMarkerList) {
            mapView.mapScene.removeMapMarker(mapMarker)
        }
        mapMarkerList.clear()
    }

    private fun showDialog(title: String, message: String) {
        val builder =
            AlertDialog.Builder(context)
        builder.setTitle(title)
        builder.setMessage(message)
        builder.show()
    }

    fun focusOnPlaceWithMarker(geoCoordinates: GeoCoordinates) {
        val mapMeasureZoom = MapMeasure(MapMeasure.Kind.DISTANCE, 200.0)
        camera.lookAt(geoCoordinates, mapMeasureZoom)
        updateCenterMarkerPosition()
        getAddressForCoordinates(geoCoordinates)
    }

    companion object {
        private val LOG_TAG: String = SearchExample::class.java.name
    }
}