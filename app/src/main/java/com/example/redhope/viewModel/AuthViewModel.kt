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


val passwordPattern =
    Regex("^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@#\$%^&+=!]).{8,}$")
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

        if (!passwordPattern.matches(state.password)) {
            state = state.copy(
                passwordError = "Password must contain uppercase, lowercase, number, special character and be at least 8 characters"
            )
            valid = false
        }

        if (isSignUp && state.confirmPassword != state.password) {
            state = state.copy(confirmPasswordError = "Passwords do not match")
            valid = false
        }

        _uiState.value = state
        return valid
    }


    fun signUp(
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit
    ) {
        if (!validate(true)) return

        _uiState.value = _uiState.value.copy(isLoading = true)

        auth.createUserWithEmailAndPassword(
            _uiState.value.email,
            _uiState.value.password
        ).addOnCompleteListener { task ->

            _uiState.value = _uiState.value.copy(isLoading = false)

            if (task.isSuccessful) {

                val user = auth.currentUser

                // 🔹 Step 1: Send Email Verification
                user?.sendEmailVerification()
                    ?.addOnSuccessListener {

                        val uid = user.uid

                        val map = hashMapOf(
                            "uid" to uid,
                            "fullName" to _uiState.value.fullName,
                            "email" to _uiState.value.email,
                            "isAvailable" to false,
                            "lastDisabledAt" to null,
                            "cooldownHours" to 6,
                            "createdAt" to System.currentTimeMillis(),
                            "isEmailVerified" to false   // 🔹 track verification
                        )

                        // 🔹 Step 2: Save user in Firestore
                        firestore.collection("users")
                            .document(uid)
                            .set(map)
                            .addOnSuccessListener {
                                onSuccess()  // show message like "Check your email"
                            }
                            .addOnFailureListener { e ->
                                onFailure("Firestore error: ${e.message}")
                            }

                    }
                    ?.addOnFailureListener { e ->
                        onFailure("Failed to send verification email: ${e.message}")
                    }

            } else {
                onFailure(task.exception?.message ?: "Signup failed")
                Log.e("SIGNUP_ERROR", "Failed: ", task.exception)
            }
        }
    }


    fun login(
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit
    ) {
        if (!validate(false)) return

        _uiState.value = _uiState.value.copy(isLoading = true)

        auth.signInWithEmailAndPassword(
            _uiState.value.email,
            _uiState.value.password
        ).addOnCompleteListener { task ->

            if (task.isSuccessful) {

                val user = auth.currentUser


                user?.reload()?.addOnCompleteListener {

                    if (user.isEmailVerified) {
                        _uiState.value = _uiState.value.copy(isLoading = false)
                        onSuccess()
                    } else {
                        auth.signOut()

                        _uiState.value = _uiState.value.copy(isLoading = false)
                        onFailure("Please verify your email before logging in")
                    }
                }

            } else {
                _uiState.value = _uiState.value.copy(isLoading = false)
                onFailure(task.exception?.message ?: "Login failed")
            }
        }
    }

    fun resendVerification(onResult: (String) -> Unit) {
        val user = FirebaseAuth.getInstance().currentUser

        if (user == null) {
            onResult("No user found. Please sign up first.")
            return
        }

        user.sendEmailVerification()
            .addOnSuccessListener {
                onResult("Verification email sent successfully")
            }
            .addOnFailureListener {
                onResult("Failed: ${it.message}")
            }
    }

    fun checkEmailVerified(
        onVerified: () -> Unit,
        onNotVerified: () -> Unit
    ) {
        val user = auth.currentUser ?: return

        user.reload().addOnCompleteListener {
            if (user.isEmailVerified) {
                onVerified()
            } else {
                onNotVerified()
            }
        }
    }
}

