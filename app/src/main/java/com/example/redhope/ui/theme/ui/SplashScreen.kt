package com.example.redhope.ui.theme.ui

import android.R
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth

@Composable
fun SplashScreen(onNavigateToHome: () -> Unit,onNavigateToLogin:()->Unit){
    val auth = FirebaseAuth.getInstance()
    LaunchedEffect(Unit){
        kotlinx.coroutines.delay(2000)
        if(auth.currentUser !=null){
            onNavigateToHome()
        }else{
            onNavigateToLogin()
        }
    }
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text("Donate App", style = MaterialTheme.typography.headlineMedium, color = MaterialTheme.colorScheme.onBackground )
    }
}