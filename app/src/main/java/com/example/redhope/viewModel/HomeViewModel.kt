package com.example.redhope.viewModel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.redhope.modal.DonationHistory
import com.example.redhope.modal.DonationRequest
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class HomeViewModel : ViewModel(){
    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    private val _userName = mutableStateOf<String?>(null)
    val userName: State<String?> = _userName

    private val _incomingRequests = MutableStateFlow<List<DonationRequest>>(emptyList())
    val incomingRequests: StateFlow<List<DonationRequest>> = _incomingRequests

    private val _myRequests = MutableStateFlow<List<DonationRequest>>(emptyList())
    val myRequests: StateFlow<List<DonationRequest>> = _myRequests

    private val _history = MutableStateFlow<List<DonationHistory>>(emptyList())
    val history: StateFlow<List<DonationHistory>> = _history


    init {
        loadUserName()
    }

    private fun loadUserName() {
        val uid = auth.currentUser?.uid ?: return

        firestore.collection("users")
            .document(uid)
            .get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    _userName.value = document.getString("fullName")


                }
            }
            .addOnFailureListener {
                _userName.value = "User"
            }
    }

    fun listenIncomingRequests(userId: String) {


        firestore.collection("donation_requests")
            .whereEqualTo("donorId", userId)
            .whereEqualTo("status", "pending")
            .addSnapshotListener { snapshot, _ ->

                val list = snapshot?.documents?.map {
                    it.toObject(DonationRequest::class.java)!!.copy(id = it.id)
                } ?: emptyList()

                _incomingRequests.value = list
            }
    }

    fun listenRequesterRequests(userId: String) {

        firestore.collection("donation_requests")
            .whereEqualTo("receiverId", userId)
            .addSnapshotListener { snapshot, _ ->

                val list = snapshot?.documents?.map {
                    it.toObject(DonationRequest::class.java)!!.copy(id = it.id)
                } ?: emptyList()

                _myRequests.value = list
            }
    }

    fun acceptRequest(requestId: String) {
        FirebaseFirestore.getInstance()
            .collection("donation_requests")
            .document(requestId)
            .update(
                mapOf(
                    "status" to "accepted",
                    "donorAccepted" to true
                )
            )
    }

    fun rejectRequest(requestId: String) {
        FirebaseFirestore.getInstance()
            .collection("donation_requests")
            .document(requestId)
            .update("status", "rejected")
    }


    fun receiverComplete(requestId: String) {

        val db = FirebaseFirestore.getInstance()

        db.collection("donation_requests")
            .document(requestId)
            .update(
                mapOf(
                    "receiverCompleted" to true,
                    "status" to "completed"
                )
            )
            .addOnSuccessListener {
                saveHistory(requestId)
            }
    }

    fun cancelRequest(requestId: String) {

        firestore.collection("donation_requests")
            .document(requestId)
            .update("status", "cancelled")
    }
//    fun donorComplete(requestId: String) {
//
//        val db = FirebaseFirestore.getInstance()
//
//        db.collection("donation_requests")
//            .document(requestId)
//            .update("donorCompleted", true)
//            .addOnSuccessListener {
//                checkAndSaveHistory(requestId)
//            }
//    }
fun saveHistory(requestId: String) {

    val db = FirebaseFirestore.getInstance()

    db.collection("donation_requests")
        .document(requestId)
        .get()
        .addOnSuccessListener { doc ->

            val request = doc.toObject(DonationRequest::class.java) ?: return@addOnSuccessListener

            val history = hashMapOf(
                "donorId" to request.donorId,
                "receiverId" to request.receiverId,
                "donorName" to request.donorName,
                "receiverName" to request.receiverName,
                "bloodGroup" to request.bloodGroup,
                "completedAt" to Timestamp.now()
            )

            db.collection("donation_history").add(history)
        }
}

    fun loadDonationHistory(userId: String) {

        firestore.collection("donation_history")
            .whereEqualTo("donorId", userId)
            .addSnapshotListener { snapshot, _ ->

                val list = snapshot?.documents?.map {
                    it.toObject(DonationHistory::class.java)!!
                } ?: emptyList()

                _history.value = list
            }
    }

//    fun checkAndSaveHistory(requestId: String) {
//
//        val db = FirebaseFirestore.getInstance()
//
//        db.collection("donation_requests")
//            .document(requestId)
//            .get()
//            .addOnSuccessListener { doc ->
//
//                val request = doc.toObject(DonationRequest::class.java) ?: return@addOnSuccessListener
//
//                if (request.donorCompleted && request.receiverCompleted && request.status != "completed") {
//
//                    // ✅ Save history
//                    val history = hashMapOf(
//                        "donorId" to request.donorId,
//                        "receiverId" to request.receiverId,
//                        "donorName" to request.donorName,
//                        "receiverName" to request.receiverName,
//                        "bloodGroup" to request.bloodGroup,
//                        "completedAt" to Timestamp.now()
//                    )
//
//                    db.collection("donation_history").add(history)
//
//                    // ✅ mark request completed
//                    db.collection("donation_requests")
//                        .document(requestId)
//                        .update("status", "completed")
//                }
//            }
//    }




}