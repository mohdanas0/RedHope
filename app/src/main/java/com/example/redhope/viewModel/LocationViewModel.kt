package com.example.redhope.viewModel

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class LocationViewModel: ViewModel() {
    private val _location = MutableStateFlow<Pair<Double, Double>?>(null)
    val location: StateFlow<Pair<Double, Double>?> = _location

    @SuppressLint("MissingPermission")
    fun fetchLocation(context: Context) {

        val fusedLocationClient =
            LocationServices.getFusedLocationProviderClient(context)

        fusedLocationClient.lastLocation
            .addOnSuccessListener { location ->

                if (location != null) {

                    Log.d("LOCATION", "Last location used")

                    _location.value =
                        Pair(location.latitude, location.longitude)

                } else {

                    fusedLocationClient.getCurrentLocation(
                        Priority.PRIORITY_HIGH_ACCURACY,
                        null
                    ).addOnSuccessListener { freshLocation ->

                        freshLocation?.let {

                            Log.d("LOCATION", "Fresh location used")

                            _location.value =
                                Pair(it.latitude, it.longitude)
                        }
                    }
                }
            }
    }

    fun clearLocation() {
        _location.value = null
    }
}