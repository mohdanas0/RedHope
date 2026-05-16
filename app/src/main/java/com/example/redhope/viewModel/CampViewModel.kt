package com.example.redhope.viewModel



import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import com.example.redhope.modal.BloodCamp
import com.google.firebase.firestore.FirebaseFirestore

class CampViewModel : ViewModel() {

    var campList = mutableStateListOf<BloodCamp>()

    init {
        fetchCamps()
    }

    private fun fetchCamps() {

        FirebaseFirestore.getInstance()
            .collection("blood_camps")
            .get()
            .addOnSuccessListener { result ->

                campList.clear()

                for (document in result) {

                    val camp =
                        document.toObject(BloodCamp::class.java)

                    campList.add(camp)
                }
            }
    }
}