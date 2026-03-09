package com.example.redhope.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.redhope.modal.DonorUIModel
import com.example.redhope.modal.FindDonorQuery
import com.example.redhope.modal.FindDonorUiState
import com.example.redhope.modal.UserFirestoreModel
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update


class FindDonorViewModel : ViewModel() {

    private val db = FirebaseFirestore.getInstance()

    private val _uiState = MutableStateFlow(FindDonorUiState())
    val uiState: StateFlow<FindDonorUiState> = _uiState

    // 📍 Store requester location
    fun setUserLocation(lat: Double, lng: Double) {
        _uiState.update {
            it.copy(currentLat = lat, currentLng = lng)
        }
    }

    // 🩸 Update selected blood group
    fun updateBloodGroup(group: String) {
        _uiState.update { it.copy(selectedBloodGroup = group) }
    }

    // 🔎 Search nearest donors
    fun searchNearestDonors() {

        val lat = _uiState.value.currentLat
        val lng = _uiState.value.currentLng
        val bloodGroup = _uiState.value.selectedBloodGroup

        if (lat == null || lng == null) {
            _uiState.update { it.copy(error = "Location not available") }
            return
        }

        if (bloodGroup.isBlank()) {
            _uiState.update { it.copy(error = "Select blood group") }
            return
        }

        _uiState.update { it.copy(isLoading = true, error = null) }

        db.collection("users")
            .whereEqualTo("bloodGroup", bloodGroup)
            .whereEqualTo("isAvailable", true)
            .get()
            .addOnSuccessListener { snapshot ->

                val donors = snapshot.documents.mapNotNull {
                    it.toObject(UserFirestoreModel::class.java)
                }

                val nearest = donors.mapNotNull { donor ->

                    if (donor.lat == null || donor.lng == null) return@mapNotNull null

                    val distance = calculateDistanceKm(
                        lat, lng,
                        donor.lat, donor.lng
                    )

                    DonorUIModel(
                        uid = donor.uid,
                        name = donor.fullName,
                        bloodGroup = donor.bloodGroup,
                        phone = donor.phone,
                        distanceKm = distance
                    )
                }
                    .sortedBy { it.distanceKm }
                    .take(10)

                _uiState.update {
                    it.copy(
                        donors = nearest,
                        isLoading = false
                    )
                }
            }
            .addOnFailureListener {
                _uiState.update {
                    it.copy(
                        error = "Failed to load donors",
                        isLoading = false
                    )
                }
            }
    }

    // 📏 Haversine formula
    private fun calculateDistanceKm(
        lat1: Double,
        lng1: Double,
        lat2: Double,
        lng2: Double
    ): Double {

        val earthRadius = 6371.0

        val dLat = Math.toRadians(lat2 - lat1)
        val dLng = Math.toRadians(lng2 - lng1)

        val a =
            kotlin.math.sin(dLat / 2) * kotlin.math.sin(dLat / 2) +
                    kotlin.math.cos(Math.toRadians(lat1)) *
                    kotlin.math.cos(Math.toRadians(lat2)) *
                    kotlin.math.sin(dLng / 2) * kotlin.math.sin(dLng / 2)

        val c = 2 * kotlin.math.atan2(
            kotlin.math.sqrt(a),
            kotlin.math.sqrt(1 - a)
        )

        return earthRadius * c
    }
}
