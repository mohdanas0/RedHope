package com.example.redhope.viewModel

import android.app.Application
import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel

import androidx.lifecycle.ViewModel
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.redhope.modal.ProfileUiState
import com.example.redhope.util.CooldownWorker
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import com.google.firebase.Timestamp
import java.util.concurrent.TimeUnit

class ProfileViewModel(application: Application): AndroidViewModel(application) {

    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    private var _uiState = MutableStateFlow(ProfileUiState())
    var uiState: StateFlow<ProfileUiState> = _uiState


    fun onFieldChange(field: String,value: String){
        _uiState.value = when(field){
            "fullName" -> _uiState.value.copy(fullName = value, fullNameError = null)
            "bloodGroup" -> _uiState.value.copy(bloodGroup = value, bloodGroupError = null)
            "phone" -> _uiState.value.copy(phone = value, phoneError = null)
            "city"-> _uiState.value.copy(city = value, cityError = null )
            "pincode"->_uiState.value.copy(pincode = value, pincodeError = null )
            else -> _uiState.value
        }
    }






    fun loadProfile() {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val firestore = FirebaseFirestore.getInstance()

        firestore.collection("users")
            .document(uid)
            .addSnapshotListener { document, error ->

                if (error != null || document == null || !document.exists()) return@addSnapshotListener

                // 🔹 Migration for old users (runs only once)
                if (!document.contains("cooldownHours") || !document.contains("lastDisabledAt")) {
                    firestore.collection("users")
                        .document(uid)
                        .update(
                            mapOf(
                                "cooldownHours" to 6,
                                "lastDisabledAt" to null
                            )
                        )
                }

                _uiState.value = _uiState.value.copy(
                    fullName = document.getString("fullName") ?: "",
                    phone = document.getString("phone") ?: "",
                    bloodGroup = document.getString("bloodGroup") ?: "",
                    city = document.getString("city") ?: "",
                    pincode = document.getString("pincode") ?: "",

                    isAvailable = document.getBoolean("isAvailable") ?: false,
                    lastDisabledAt = document.getLong("lastDisabledAt"),
                    cooldownHours = (document.getLong("cooldownHours") ?: 6L).toInt()
                )
            }
    }




    fun saveProfile(onSuccess: () -> Unit, onFailure: (String) -> Unit) {
        val uid = auth.currentUser?.uid ?: return

        // Basic validation
        if (_uiState.value.fullName.isBlank() || _uiState.value.bloodGroup.isBlank() || _uiState.value.phone.isBlank() || _uiState.value.city.isBlank() || _uiState.value.pincode.isBlank() ) {
            _uiState.value = _uiState.value.copy(
                fullNameError = if (_uiState.value.fullName.isBlank()) "Name required" else null,
                bloodGroupError = if (_uiState.value.bloodGroup.isBlank()) "Select blood group" else null,
                phoneError = if (_uiState.value.phone.isBlank()) "Phone required" else null,
                cityError = if (_uiState.value.city.isBlank()) "City Required" else null,
                pincodeError = if (_uiState.value.pincode.isBlank()) "Pincode required" else null,
            )
            return
        }

        _uiState.value = _uiState.value.copy(isLoading = true)

        val userMap = mapOf(
            "fullName" to _uiState.value.fullName,
            "bloodGroup" to _uiState.value.bloodGroup,
            "phone" to _uiState.value.phone,
            "city" to _uiState.value.city,
            "pincode" to _uiState.value.pincode
        )

        firestore.collection("users")
            .document(uid)
            .set(userMap, SetOptions.merge())
            .addOnSuccessListener {
                _uiState.value = _uiState.value.copy(isLoading = false, isSaved = true)
                onSuccess()
            }
            .addOnFailureListener { e ->
                _uiState.value = _uiState.value.copy(isLoading = false)
                onFailure(e.message ?: "Error saving profile")
            }
    }

    fun updateAvailability(value: Boolean,latitude: Double?,longitude:Double?) {
        val uid = auth.currentUser?.uid ?: return

        val updates = mutableMapOf<String, Any?>(
            "isAvailable" to value
        )


        if (value) {
            updates["lastDisabledAt"] = null
            Log.d("AVAILABILITY", "Updating to: $value")
            if (latitude != null && longitude != null) {
                updates["lat"] = latitude
                updates["lng"] = longitude
                updates["locationUpdatedAt"] = Timestamp.now()

            }

        } else {
            val currentTime = System.currentTimeMillis()
            updates["lastDisabledAt"] = currentTime
            Log.d("AVAILABILITY", "Updating to: $value")

            val cooldownHours = _uiState.value.cooldownHours
            val cooldownMillis = cooldownHours * 60 * 60 * 1000L

            scheduleCooldownNotification(cooldownMillis)
        }

        firestore.collection("users")
            .document(uid)
            .update(updates)
    }

    fun canEnableAvailability(
        lastDisabledAt: Long?,
        cooldownHours: Int = 6
    ): Pair<Boolean, String?> {

        if (lastDisabledAt == null) {
            return true to null
        }

        val cooldownMillis = cooldownHours * 60 * 60 * 1000L
        val elapsed = System.currentTimeMillis() - lastDisabledAt

        return if (elapsed >= cooldownMillis) {
            true to null
        } else {
            val remainingMillis = cooldownMillis - elapsed
            val remainingMinutes = remainingMillis / (60 * 1000)

            false to "Please wait $remainingMinutes minutes before becoming available again."
        }
    }

    private fun scheduleCooldownNotification(delayMillis: Long) {

        val workRequest = OneTimeWorkRequestBuilder<CooldownWorker>()
            .setInitialDelay(delayMillis, TimeUnit.MILLISECONDS)
            .build()

        WorkManager.getInstance(getApplication())
            .enqueue(workRequest)
    }

//    fun updateLocation(latitude: Double, longitude: Double) {
//
//        val userId = auth.currentUser?.uid ?: return
//
//        FirebaseFirestore.getInstance()
//            .collection("users")
//            .document(userId)
//            .update(
//                mapOf(
//                    "lat" to latitude,
//                    "lng" to longitude,
//                    "locationUpdatedAt" to Timestamp.now()
//                )
//            )
//    }
}

