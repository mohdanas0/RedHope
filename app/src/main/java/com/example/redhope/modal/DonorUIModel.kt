package com.example.redhope.modal

import com.google.firebase.Timestamp

data class DonorUIModel(
    val uid: String,
    val name: String,
    val bloodGroup: String,
    val phone: String,
    val distanceKm: Double,
    val locationUpdatedAt: Timestamp? = null

)
