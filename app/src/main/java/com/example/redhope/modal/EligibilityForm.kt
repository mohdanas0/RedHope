package com.example.redhope.modal

data class EligibilityForm(
    val age: Int,
    val weight: Int,
    val lastDonationMonthsAgo: Int,
    val hasIllness: Boolean,
    val hasRecentSurgery: Boolean,
    val onMedication: Boolean
)
