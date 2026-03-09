package com.example.redhope.modal

data class UserFirestoreModel(
    val uid: String = "",
    val fullName: String = "",
    val bloodGroup: String = "",
    val city: String = "",
    val pincode: String = "",
    val phone: String = "",
    val isAvailable: Boolean = false,
    val lastDisabledAt: Long? = null,
    val cooldownHours: Int = 6,
    val createdAt: Long = 0L,
    val lat: Double?=null,
    val lng: Double?=null,
)
