package com.example.redhope.util

import android.Manifest
import android.app.Service
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.os.Looper
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.annotation.RequiresPermission

import androidx.core.app.NotificationCompat

import com.google.android.gms.location.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.Timestamp

class LocationForegroundService : Service() {

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()


    @RequiresPermission(allOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate() {
        super.onCreate()

        fusedLocationClient =
            LocationServices.getFusedLocationProviderClient(this)

        startForegroundNotification()
        startLocationUpdates()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun startForegroundNotification() {

        val channelId = "location_channel"

        val channel = NotificationChannel(
            channelId,
            "Location Service",
            NotificationManager.IMPORTANCE_LOW
        )

        val manager =
            getSystemService(NotificationManager::class.java)

        manager.createNotificationChannel(channel)

        val notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle("Donate App")
            .setContentText("Updating your location for blood donation")
            .setSmallIcon(android.R.drawable.ic_menu_mylocation)
            .build()

        startForeground(1, notification)
    }
    @RequiresPermission(allOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    private fun startLocationUpdates() {
        Log.d("SERVICE_DEBUG", "Location updates requested")

        val request = LocationRequest.Builder(
            Priority.PRIORITY_BALANCED_POWER_ACCURACY,
            900000
        ) .setMinUpdateIntervalMillis(900000)
            .setMaxUpdateDelayMillis(900000)
            .setMinUpdateDistanceMeters(0f)
            .setWaitForAccurateLocation(false)
            .build()


        fusedLocationClient.requestLocationUpdates(
            request,
            locationCallback,
            Looper.getMainLooper()
        )
    }

    private val locationCallback = object : LocationCallback() {

        override fun onLocationResult(result: LocationResult) {

            val location = result.lastLocation ?: return
            Log.d("LOCATION_UPDATE",
                "Lat: ${location.latitude}, Lng: ${location.longitude}")
            val uid = auth.currentUser?.uid ?: return

            firestore.collection("users")
                .document(uid)
                .get()
                .addOnSuccessListener { document ->

                    val isAvailable = document.getBoolean("isAvailable") ?: false

                    if (isAvailable) {

                        firestore.collection("users")
                            .document(uid)
                            .update(
                                mapOf(
                                    "lat" to location.latitude,
                                    "lng" to location.longitude,
                                    "locationUpdatedAt" to Timestamp.now()
                                )

                            )

                    }

                }
        }
    }
    override fun onDestroy() {
        super.onDestroy()
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
