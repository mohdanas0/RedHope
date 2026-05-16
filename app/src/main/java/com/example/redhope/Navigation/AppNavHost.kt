package com.example.redhope.Navigation


import androidx.compose.runtime.Composable

import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController

import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.redhope.ui.theme.ui.BloodCompatibilityScreen

import com.example.redhope.ui.theme.ui.CampScreen
import com.example.redhope.ui.theme.ui.DonationHistoryScreen
import com.example.redhope.ui.theme.ui.EmailVerificationScreen
import com.example.redhope.ui.theme.ui.FindDonorScreen
import com.example.redhope.ui.theme.ui.HomeScreen
import com.example.redhope.ui.theme.ui.LoginScreen
import com.example.redhope.ui.theme.ui.ProfileScreen
import com.example.redhope.ui.theme.ui.SignUpScreen
import com.example.redhope.ui.theme.ui.SplashScreen
import com.example.redhope.viewModel.LocationViewModel

import com.google.firebase.auth.FirebaseAuth


sealed class Screen(val route: String){
    object Splash : Screen("Splash")
    object Login : Screen("Login")
    object SignUp : Screen("SignUp")
    object ProfileSetupScreen: Screen("Profile")
    object Home : Screen("Home")
    object FindDonorScreen : Screen("Find Donor")
    object DonationHistoryScreen : Screen("Donation History")
    object EmailVerificationScreen : Screen("EmailVerification")
    object CampScreen : Screen("CampScreen")
    object BloodCompatibilityScreen : Screen("Blood Compatibility")



}

@Composable
fun AppNavHost(navHostController: NavHostController){
    val sharedLocationVM: LocationViewModel = viewModel()
    NavHost(
        navController = navHostController,
        startDestination = Screen.Splash.route
    ){

        composable(Screen.Splash.route) {
            SplashScreen(
                onNavigateToHome = {
                    navHostController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                },
                onNavigateToLogin = {
                    navHostController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                },
                onNavigateToVerification = {
                    navHostController.navigate(Screen.EmailVerificationScreen.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Login.route) {
            LoginScreen(
                onLoginClick = {
                    navHostController.navigate(Screen.Home.route){
                        popUpTo(Screen.Login.route){inclusive = true}
                    }
                },
                onSignUpClick = {
                    navHostController.navigate(Screen.SignUp.route)
                },

            )
        }

        composable(Screen.SignUp.route){
            SignUpScreen(onSignUpSuccess = {
                navHostController.navigate(Screen.EmailVerificationScreen.route) {
                    popUpTo(Screen.SignUp.route) { inclusive = true }
                }
            },
                onLoginClick = {
                    navHostController.popBackStack()
                })
        }

        composable(Screen.EmailVerificationScreen.route) {
            EmailVerificationScreen(
                onVerified = {
                    navHostController.navigate(Screen.Home.route) {
                        popUpTo(Screen.SignUp.route) { inclusive = true }
                    }
                },
                onBack = {
                    navHostController.popBackStack()
                }
            )
        }

        composable(Screen.FindDonorScreen.route){
               FindDonorScreen(
                   locationVM = sharedLocationVM,
                   onBack = {
                   navHostController.popBackStack()
               })
        }

        composable(Screen.Home.route){

            HomeScreen(
                locationVM=sharedLocationVM,
                onLogout = {
                FirebaseAuth.getInstance().signOut()
                navHostController.navigate(Screen.Login.route){
                    popUpTo(Screen.Splash.route){inclusive = true}
                }
            }, onFindDonor = {
                navHostController.navigate(
                    Screen.FindDonorScreen.route)

            }, onProfileClick = {
                navHostController.navigate(Screen.ProfileSetupScreen.route)

            }, onHistoryClick = {
                navHostController.navigate(Screen.DonationHistoryScreen.route)

            }, onCamp = {
                    navHostController.navigate(Screen.CampScreen.route)
            }, onBloodCompatibility = {
                   navHostController.navigate(Screen.BloodCompatibilityScreen.route)
                }
            )

        }
        composable(Screen.DonationHistoryScreen.route) {
            DonationHistoryScreen (
                onBack = { navHostController.popBackStack() }
            )
        }
        composable(Screen.BloodCompatibilityScreen.route) {
            BloodCompatibilityScreen  (
                onBack = { navHostController.popBackStack() }
            )
        }

        composable(Screen.ProfileSetupScreen.route){
            ProfileScreen(onProfileSaved = {
                navHostController.navigate(Screen.Home.route){
                    popUpTo(Screen.Splash.route){inclusive = true}
                }
            }, onBackClick = {
                navHostController.popBackStack()
            })
        }

        composable(Screen.CampScreen.route){
            CampScreen( onBack = { navHostController.popBackStack() })
        }



    }
}

