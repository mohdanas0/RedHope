package com.example.redhope.ui.theme.ui

import android.Manifest
import android.R
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.text.Layout
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresPermission
import androidx.camera.camera2.pipe.core.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.redhope.common.ButtonCard
import com.example.redhope.viewModel.HomeViewModel
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import androidx.compose.material3.*
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import com.example.redhope.modal.ProfileUiState
import com.example.redhope.viewModel.ProfileViewModel
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontVariation.Settings
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.navigation.NavHostController
import com.example.redhope.modal.FindDonorQuery
import com.example.redhope.util.openNearbyBloodBanks
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority


@Composable
fun HomeScreen(
    onLogout: () -> Unit,
    onFindDonor: () -> Unit,
    onEmergencyClick: () -> Unit,
    onHistoryClick: () -> Unit,
    onProfileClick: () -> Unit

) {

    val homeVM: HomeViewModel = viewModel()
    val name = homeVM.userName.value
    val profileVM: ProfileViewModel = viewModel()
    val profileUiState by profileVM.uiState.collectAsState()
    var showFindDonorBottomSheet by remember {
        mutableStateOf(false)
    }
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    var showEligibilityPopup by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val fusedLocationClient =
        LocationServices.getFusedLocationProviderClient(context)

    fun isLocationEnabled(): Boolean {
        val locationManager =
            context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }

    fun openLocationSettings() {
        context.startActivity(Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS))
    }

    @SuppressLint("MissingPermission")
    fun getLocation() {
            fusedLocationClient.getCurrentLocation(
                Priority.PRIORITY_HIGH_ACCURACY,
                null
            ).addOnSuccessListener { location ->

                if (location != null) {

                    android.util.Log.d("LOCATION", "Lat: ${location.latitude}")

                    profileVM.updateAvailability(
                        value = true,
                        latitude = location.latitude,
                        longitude = location.longitude
                    )

                } else {
                    Toast.makeText(
                        context,
                        "Unable to get location. Please turn on GPS.",
                        Toast.LENGTH_LONG
                    ).show()

                    android.util.Log.e("LOCATION", "Location is NULL — turn on GPS")
                }
            }
    }

    val permissionLauncher =
        rememberLauncherForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { granted ->
            if (granted) {
                if (!isLocationEnabled()) {
                    openLocationSettings()
                    Toast.makeText(
                        context,
                        "Please enable location",
                        Toast.LENGTH_LONG
                    ).show()
                } else {
                    getLocation()
                }
            } else {
                Toast.makeText(
                    context,
                    "Location permission required",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    LaunchedEffect(Unit) {
        profileVM.loadProfile()
    }




    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Text(
                    "Menu",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(16.dp)
                )

                Divider()

                NavigationDrawerItem(
                    label = { Text("Profile") },
                    selected = false,
                    onClick = {
                        onProfileClick()
                        scope.launch { drawerState.close() }
                    },
                    icon = { Icon(Icons.Default.Person, contentDescription = null) }
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Available for Donation", fontSize = 16.sp)
                    Switch(
                        checked = profileUiState.isAvailable,
                        onCheckedChange = { checked ->
                            if (checked) {
                                val (allowed, message) =
                                    profileVM.canEnableAvailability(
                                        lastDisabledAt = profileUiState.lastDisabledAt,
                                        cooldownHours = profileUiState.cooldownHours
                                    )

                                if (allowed) {
                                    showEligibilityPopup = true
                                } else {
                                    scope.launch {
                                        snackbarHostState.showSnackbar(message ?: "Cooldown active")
                                    }
                                }

                            } else {
                                profileVM.updateAvailability(false,null,null)
                            }

                        }
                    )
                }


                NavigationDrawerItem(
                    label = { Text("Logout") },
                    selected = false,
                    onClick = {
                        onLogout()
                        scope.launch { drawerState.close() }
                    },
                    icon = { Icon(Icons.Default.ExitToApp, contentDescription = null) }
                )


            }
        }
    ) {
        Scaffold(
            snackbarHost = { SnackbarHost(snackbarHostState) }
        ) { padding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.primary)
            ) {

                IconButton(
                    onClick = {
                        scope.launch { drawerState.open() }
                    },
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(16.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Menu,
                        contentDescription = "Menu",
                        tint = Color.White
                    )
                }


                Text(
                    "Home",
                    color = Color.White,
                    style = MaterialTheme.typography.headlineLarge,
                    modifier = Modifier
                        .padding(top = 110.dp)
                        .align(Alignment.TopCenter)
                )


                Card(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 150.dp),
                    shape = RoundedCornerShape(topStart = 25.dp, topEnd = 25.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background),
                    elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
                ) {
                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        "Hi ${name ?: "Loading..."} 👋",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )

                    Text(
                        "Ready to donate today?",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            ButtonCard(
                                icon = com.example.redhope.R.drawable.find,
                                title = "Find Donor",
                                onClick = onFindDonor,

                                modifier = Modifier.weight(1f)
                            )

                            ButtonCard(
                                icon = com.example.redhope.R.drawable.alert,
                                title = "Emergency",
                                onClick = onEmergencyClick,
                                modifier = Modifier.weight(1f)
                            )

                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            ButtonCard(
                                icon = com.example.redhope.R.drawable.file,
                                title = "Donation",
                                onClick = onHistoryClick,
                                modifier = Modifier.weight(1f)
                            )
                            ButtonCard(
                                icon = com.example.redhope.R.drawable.nearby,
                                title = "Nearby BloodBanks",
                                onClick = { openNearbyBloodBanks(context) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }
        }

    }
//    if (showFindDonorBottomSheet) {
//        FindDonorBottomSheet(
//            onDismiss = {
//                showFindDonorBottomSheet = false
//            },
//            onSearchClick = { query ->
//                showFindDonorBottomSheet = false
//
//                onFindDonorSearch(query)
//
//            }
//        )
//    }

    if (showEligibilityPopup) {
        EligibilityPopup(
            onDismiss = { showEligibilityPopup = false },
            onEligible = {
                if (ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                    return@EligibilityPopup
                }

                // ✅ GPS check
                if (!isLocationEnabled()) {
                    openLocationSettings()
                    Toast.makeText(
                        context,
                        "Please enable location to become available",
                        Toast.LENGTH_LONG
                    ).show()
                    return@EligibilityPopup
                }

                getLocation()
                showEligibilityPopup = false

            }
        )
    }




}




@RequiresPermission(allOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
@Preview(showBackground = true)
@Composable
fun HomePreview(){
    HomeScreen(onLogout = {

    }, onFindDonor = {

    }, onProfileClick = {

    }, onHistoryClick = {

    }, onEmergencyClick = {

    })
}
