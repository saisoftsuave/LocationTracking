package org.softsuave.locationtracking

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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun LocationScreen(viewModel: LocationViewModel) {
    val locationInfo by viewModel.locationInfo.collectAsState()
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


        RadiusSelector(viewModel, distance)

    }
}


@Composable
fun RadiusSelector(viewModel: LocationViewModel, distance: Int) {
    Column(modifier = Modifier.padding(16.dp)) {
        Text("Select circle radius to track")

        // Slider range: 50 to 200 meters
        Slider(
            value = distance.toFloat(),
            onValueChange = { viewModel.setDistance(it.toInt()) },
            valueRange = 50f..200f,
            steps = 15, // Optional: for fixed intervals
            modifier = Modifier.padding(vertical = 16.dp)
        )

        Text(text = "$distance meters")

        Spacer(modifier = Modifier.height(8.dp))

        Button(onClick = { viewModel.setDistance(100) }) {
            Text("Reset to 100m")
        }
    }
}