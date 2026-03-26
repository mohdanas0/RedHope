package com.example.redhope.modal

data class DonationRequest(
    val id: String = "",
    val donorId: String = "",
    val donorName: String = "",
    val receiverId: String = "",
    val receiverName: String = "",
    val bloodGroup: String = "",

    val status: String = "pending", // pending / accepted / rejected / completed
    val donorAccepted: Boolean = false,

    val donorCompleted: Boolean = false,
    val receiverCompleted: Boolean = false
)
