package org.softsuave.locationtracking

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Slider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun LocationScreen(viewModel: LocationViewModel) {
    val locationInfo by viewModel.locationInfo.collectAsState()
    val distanceInfo by viewModel.distanceInfo.collectAsState()
    val geoFenceRunning by viewModel.geoFenceRunning.collectAsState()
    val distance by viewModel.sliderValue.collectAsState()
    val thresholdDistance by remember {
        mutableStateOf(0)
    }
    val distance by viewModel.distance.collectAsState()
    val notificationId by remember { mutableStateOf(10) }


    LaunchedEffect(locationInfo) {
        sendLocalNotification(notificationId, locationInfo)
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = locationInfo,
            style = MaterialTheme.typography.body1,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Button(onClick = { viewModel.getCurrentLocation() }) {
            Text("Get Location")
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(onClick = { viewModel.startTracking() }) {
            Text("Start Tracking")
        }
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = { viewModel.stopTracking() }) {
            Text("Stop Tracking")
        }
        RadiusSelector(viewModel)
    }
}


@Composable
fun RadiusSelector(viewModel: LocationViewModel) {
    var distance by remember { mutableStateOf(10) }
    val geoFenceInfo by viewModel.geoFenceInfo.collectAsState()
    val distanceInfo by viewModel.distanceInfo.collectAsState()
    Column(modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
        ) {
        Text("Select distance from initial location to track")

        // Slider range: 50 to 200 meters
        Slider(
            value = distance.toFloat(),
            onValueChange = { distance = it.toInt() },
            valueRange = 10f..200f,
            modifier = Modifier.padding(vertical = 16.dp)
        )

        Text(text = "selected distance: $distance meters")

        Spacer(modifier = Modifier.height(8.dp))

        Button(onClick = { distance = 10 }) {
            Text("Reset to 10m")
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text("your distance from initial location : $distanceInfo")
        Button(
            onClick = {
                viewModel.startGeoFence(
                    shapeEnum = GeoFenceShapeEnum.CIRCLE,
                    thresholdMeters = distance.toDouble()
                )
            }
        ) {
            Text("Start GeoFencing")
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text("Geofence Result : $geoFenceInfo")
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = { viewModel.stopGeoFence() }) {
            Text("Stop GeoFencing")
        }

    }
}