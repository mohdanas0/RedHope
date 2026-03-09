package com.example.redhope.modal

data class FindDonorQuery(
    val bloodGroup: String,
    val city: String,
    val pincode: String?,
    val state: String?
)
