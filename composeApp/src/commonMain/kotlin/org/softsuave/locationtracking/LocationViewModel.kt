package org.softsuave.locationtracking

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.jordond.compass.Priority
import dev.jordond.compass.geolocation.Geolocator
import dev.jordond.compass.geolocation.GeolocatorResult
import dev.jordond.compass.geolocation.LocationRequest
import dev.jordond.compass.geolocation.TrackingStatus
import dev.jordond.compass.geolocation.mobile
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class LocationViewModel() : ViewModel() {
    private val geolocator: Geolocator = Geolocator.mobile()

    private val _locationInfo = MutableStateFlow("Press the button to get location")
    val locationInfo: StateFlow<String> = _locationInfo

    val trackingStatus = MutableStateFlow<TrackingStatus>(TrackingStatus.Idle)

    init {
        viewModelScope.launch {
            geolocator.trackingStatus.collectLatest { status ->
                trackingStatus.value = status
                when (status) {
                    is TrackingStatus.Tracking -> _locationInfo.value = "Tracking started"
                    is TrackingStatus.Error -> _locationInfo.value = "Error: ${status.cause}"
                    is TrackingStatus.Idle -> _locationInfo.value = "Tracking stopped"
                    is TrackingStatus.Update -> {
                        val location = status.location.coordinates
                        _locationInfo.value =
                            "Latitude: ${location.latitude}, Longitude: ${location.longitude}"
                    }
                }
            }
        }
    }

    fun getCurrentLocation() {
        viewModelScope.launch {
            when (val result = geolocator.current()) {
                is GeolocatorResult.Success -> {
                    val location = result.data.coordinates
                    _locationInfo.value =
                        "Latitude: ${location.latitude}, Longitude: ${location.longitude}"
                }

                is GeolocatorResult.Error -> {
                    _locationInfo.value = when (result) {
                        is GeolocatorResult.NotSupported -> "Geolocation not supported"
                        is GeolocatorResult.NotFound -> "Location not found"
                        is GeolocatorResult.PermissionError -> "Permission denied"
                        is GeolocatorResult.GeolocationFailed -> "Failed: ${result.message}"
                        else -> "Error"
                    }
                }
            }
        }
    }

    fun startTracking() {
        viewModelScope.launch {
            geolocator.startTracking(
                LocationRequest(
                    interval = 1000L,
                    priority = Priority.HighAccuracy
                )
            )
        }
    }
}
