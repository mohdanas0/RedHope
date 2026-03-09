package com.example.redhope

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import com.example.redhope.Navigation.AppNavHost
import com.example.redhope.ui.theme.RedHopeTheme
import com.example.redhope.ui.theme.ui.LoginScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            RedHopeTheme {
                Surface(
                    modifier = Modifier.fillMaxSize()
                        .background(MaterialTheme.colorScheme.background)

                ){
                    val navController = rememberNavController()
                    AppNavHost(navHostController = navController)

                }

            }
        }
    }
}



@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    RedHopeTheme {
        LoginScreen( onLoginClick = {
            // Just print dummy values in Preview (won’t actually run)
            println("Login clicked ")
        }, onGoogleLogin = {println("Google clicked")}, onSignUpClick = {println("Signup clicked")})
    }
}