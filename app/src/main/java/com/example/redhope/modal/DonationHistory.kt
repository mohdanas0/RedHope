package com.example.redhope.modal

import com.google.firebase.Timestamp

data class DonationHistory(
    val donorId: String = "",
    val receiverId: String = "",
    val donorName: String = "",
    val receiverName: String = "",
    val bloodGroup: String = "",
    val completedAt: Timestamp? = null
)
