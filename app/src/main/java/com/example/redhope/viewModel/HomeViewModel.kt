package com.example.redhope.viewModel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class HomeViewModel : ViewModel(){
    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    private val _userName = mutableStateOf<String?>(null)
    val userName: State<String?> = _userName




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





}