package com.example.redhope.modal

data class FindDonorUiState (
    val selectedBloodGroup: String = "",
    val currentLat: Double? = null,
    val currentLng: Double? = null,
    val donors: List<DonorUIModel> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)