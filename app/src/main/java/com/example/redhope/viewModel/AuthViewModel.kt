package com.example.redhope.viewModel


import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.redhope.modal.AuthUiState
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore

class AuthViewModel : ViewModel(){

    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    private var _uiState = mutableStateOf(AuthUiState())
    val uiState: State<AuthUiState> = _uiState

    fun onFieldChange(field: String,value: String){
        _uiState.value = when(field){
            "fullName" -> _uiState.value.copy(fullName = value, fullNameError = null)
            "email" -> _uiState.value.copy(email = value, emailError = null)
            "password" -> _uiState.value.copy(password = value, passwordError = null, confirmPasswordError = null)
            "confirmPassword" -> _uiState.value.copy(confirmPassword = value, confirmPasswordError = null)
            else -> _uiState.value
        }
    }

    fun validate(isSignUp: Boolean): Boolean{
        var valid = true
        var state = _uiState.value

        if (isSignUp && state.fullName.isBlank()) {
            state = state.copy(fullNameError = "Full name required")
            valid = false
        }

        if (state.email.isBlank() || !android.util.Patterns.EMAIL_ADDRESS.matcher(state.email).matches()) {
            state = state.copy(emailError = "Valid email required")
            valid = false
        }

        if (state.password.length < 6) {
            state = state.copy(passwordError = "Password must be at least 6 characters")
            valid = false
        }

        if (isSignUp && state.confirmPassword != state.password) {
            state = state.copy(confirmPasswordError = "Passwords do not match")
            valid = false
        }

        _uiState.value = state
        return valid
    }


    fun signUp(onSuccess: () -> Unit,onFailure: (String) -> Unit) {
        if (!validate(true)) return
        _uiState.value = _uiState.value.copy(isLoading = true)

        auth.createUserWithEmailAndPassword(_uiState.value.email, _uiState.value.password)
            .addOnCompleteListener { task ->
                _uiState.value = _uiState.value.copy(isLoading = false)
                if (task.isSuccessful) {
                    val uid = auth.currentUser?.uid ?: return@addOnCompleteListener
                    val map = hashMapOf(
                        "uid" to uid,
                        "fullName" to _uiState.value.fullName,
                        "email" to _uiState.value.email,
                        "isAvailable" to false,
                        "lastDisabledAt" to null,
                        "cooldownHours" to 6,
                        "createdAt" to System.currentTimeMillis()
                    )
                    firestore.collection("users")
                        .document(uid)
                        .set(map)
                        .addOnSuccessListener {
                            _uiState.value = _uiState.value.copy(isLoading = false)
                            onSuccess()
                        }
                        .addOnFailureListener { e ->
                            _uiState.value = _uiState.value.copy(isLoading = false)
                            onFailure("Firestore error: ${e.message}")
                        }


                }
                else{
                    _uiState.value = _uiState.value.copy(isLoading = false)
                    onFailure(task.exception?.message ?: "Signup failed")
                    Log.e("SIGNUP_ERROR", "Failed: ", task.exception)
                }
            }
    }


    fun login(onSuccess: () -> Unit) {
        if (!validate(false)) return
        _uiState.value = _uiState.value.copy(isLoading = true)

        auth.signInWithEmailAndPassword(_uiState.value.email, _uiState.value.password)
            .addOnCompleteListener { task ->
                _uiState.value = _uiState.value.copy(isLoading = false)
                if (task.isSuccessful) onSuccess()
                else _uiState.value = _uiState.value.copy(emailError = task.exception?.message)
            }
    }

}

