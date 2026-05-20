package com.example.redhope.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.redhope.modal.DonorUIModel
import com.example.redhope.modal.FindDonorUiState
import com.example.redhope.modal.UserFirestoreModel
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update


class FindDonorViewModel : ViewModel() {

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private val _uiState = MutableStateFlow(FindDonorUiState())
    val uiState: StateFlow<FindDonorUiState> = _uiState

    val currentUid = FirebaseAuth.getInstance().currentUser?.uid


    fun setUserLocation(lat: Double, lng: Double) {
        _uiState.update {
            it.copy(currentLat = lat, currentLng = lng)
        }
    }

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

        val MAX_DISTANCE_KM = 20   // ✅ radius limit

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

                    if (distance > MAX_DISTANCE_KM) return@mapNotNull null

                    if (donor.uid == currentUid) return@mapNotNull null

                    DonorUIModel(
                        uid = donor.uid,
                        name = donor.fullName,
                        bloodGroup = donor.bloodGroup,
                        phone = donor.phone,
                        distanceKm = distance,
                        locationUpdatedAt = donor.locationUpdatedAt
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

fun sendDonationRequest(
    donorId: String,
    donorName: String,
    bloodGroup: String
) {

    val currentUser = auth.currentUser ?: return

    db.collection("users")
        .document(currentUser.uid)
        .get()
        .addOnSuccessListener { document ->

            val receiverName =
                document.getString("fullName") ?: "Unknown"

            val request = hashMapOf(

                "donorId" to donorId,
                "donorName" to donorName,

                "receiverId" to currentUser.uid,
                "receiverName" to receiverName,

                "bloodGroup" to bloodGroup,

                "status" to "pending",

                "donorAccepted" to false,
                "donorCompleted" to false,
                "receiverCompleted" to false,

                "createdAt" to Timestamp.now()
            )

            db.collection("donation_requests")
                .add(request)
        }
}
}
