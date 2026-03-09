package com.example.redhope.modal



data class ProfileUiState(
    val fullName: String = "",
    val fullNameError: String?=null,

    val bloodGroup: String="",
    val bloodGroupError: String?=null,


    val phone: String = "",
    val phoneError: String? = null,

    val isLoading: Boolean = false,
    val isSaved: Boolean = false,

    val city: String = "",
    val cityError: String?=null,

    val pincode:String = "",
    val pincodeError:String?=null,

    val isAvailable: Boolean = false,
    val lastDisabledAt: Long? = null,
    val cooldownHours: Int = 6,

    val lat: Double?=null,
    val lng: Double?=null,

)
