package com.example.redhope.ui.theme.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.example.redhope.R
import com.google.firebase.auth.FirebaseAuth

@Composable
fun SplashScreen(
    onNavigateToHome: () -> Unit,
    onNavigateToLogin: () -> Unit,
    onNavigateToVerification: () -> Unit
) {
    val auth = FirebaseAuth.getInstance()

    LaunchedEffect(Unit) {
        kotlinx.coroutines.delay(2000)

        val user = auth.currentUser

        if (user != null) {

            user.reload().addOnCompleteListener {

                if (user.isEmailVerified) {
                    onNavigateToHome()
                } else {
                    onNavigateToVerification()
                }
            }
        } else {
            onNavigateToLogin()
        }
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Image(
            painter = painterResource(id = R.drawable.splash),
            contentDescription = "Splash Image",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
    }
}