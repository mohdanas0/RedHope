package com.example.redhope.ui.theme.ui

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
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
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.redhope.common.BloodGroupDropdown
import com.example.redhope.common.DonorCard
import com.example.redhope.modal.FindDonorQuery
import com.example.redhope.modal.FindDonorUiState
import com.example.redhope.viewModel.FindDonorViewModel
import com.example.redhope.viewModel.LocationViewModel
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FindDonorScreen(
    locationVM: LocationViewModel,
    onBack: () -> Unit
) {

    val viewModel: FindDonorViewModel = viewModel()


    val uiState by viewModel.uiState.collectAsState()
    val location by locationVM.location.collectAsState()


    val context = LocalContext.current

    fun isLocationEnabled(): Boolean {
        val locationManager =
            context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }





    LaunchedEffect(location) {
        location?.let {
            viewModel.setUserLocation(it.first, it.second)
        }
    }


    val showEmptyMessage =
        uiState.selectedBloodGroup.isNotEmpty() &&
                !uiState.isLoading &&
                uiState.donors.isEmpty()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Available Donors") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {

            Text(
                text = "Find Donor",
                style = MaterialTheme.typography.headlineMedium
            )

            Spacer(modifier = Modifier.height(16.dp))

            BloodGroupDropdown(
                selectedBloodGroup = uiState.selectedBloodGroup,
                onSelected = { group ->
                    viewModel.updateBloodGroup(group)
                    viewModel.searchNearestDonors()
                },
                enabled = location != null
            )

            Spacer(modifier = Modifier.height(16.dp))


            if (location == null) {
                if (!isLocationEnabled()) {
                        Text("Location is OFF. Please enable location from Home screen.")
                } else {

                    Text("Detecting your location...")

                    Spacer(modifier = Modifier.height(8.dp))

                    CircularProgressIndicator()
                }
            }

            if (uiState.isLoading) {
                CircularProgressIndicator()
            }

            uiState.error?.let {
                Text(
                    text = it,
                    color = Color.Red
                )
            }

            if (showEmptyMessage) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No donors found for ${uiState.selectedBloodGroup}",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }

            if (uiState.donors.isNotEmpty()) {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(uiState.donors) { donor ->
                        DonorCard(donor, onRequestClick = {viewModel.sendDonationRequest(donor.uid, donorName = donor.name, bloodGroup = donor.bloodGroup)})
                    }
                }
            }
        }
    }
}



