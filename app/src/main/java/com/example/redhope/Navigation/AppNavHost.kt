package com.example.redhope.Navigation

import androidx.compose.foundation.gestures.ScrollableState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ComposeNode
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.redhope.modal.FindDonorQuery
import com.example.redhope.ui.theme.ui.DonationHistoryScreen
import com.example.redhope.ui.theme.ui.FindDonorScreen
import com.example.redhope.ui.theme.ui.HomeScreen
import com.example.redhope.ui.theme.ui.LoginScreen
import com.example.redhope.ui.theme.ui.ProfileScreen
import com.example.redhope.ui.theme.ui.SignUpScreen
import com.example.redhope.ui.theme.ui.SplashScreen
import com.example.redhope.viewModel.LocationViewModel
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth


sealed class Screen(val route: String){
    object Splash : Screen("Splash")
    object Login : Screen("Login")
    object SignUp : Screen("SignUp")
    object ProfileSetupScreen: Screen("Profile")
    object Home : Screen("Home")
    object FindDonorScreen : Screen("Find Donor")
    object DonationHistoryScreen : Screen("Donation History")


//    object FindDonorScreen : Screen(
//        "find_donor_result/{bloodGroup}/{city}?pincode={pincode}&state={state}"
//    ) {
//        fun createRoute(
//            bloodGroup: String,
//            city: String,
//            pincode: String?,
//            state: String?
//        ): String {
//            return "find_donor_result/$bloodGroup/$city" +
//                    "?pincode=${pincode ?: ""}" +
//                    "&state=${state ?: ""}"
//        }
//    }

}

@Composable
fun AppNavHost(navHostController: NavHostController){
    val sharedLocationVM: LocationViewModel = viewModel()
    NavHost(
        navController = navHostController,
        startDestination = Screen.Splash.route
    ){

        composable(Screen.Splash.route) {
            SplashScreen(onNavigateToHome = {
                navHostController.navigate(Screen.Home.route){
                    popUpTo(Screen.Splash.route){inclusive = true}
                }

            }, onNavigateToLogin = {
                navHostController.navigate(Screen.Login.route){
                    popUpTo(Screen.Splash.route){inclusive = true}
                }
            })
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
                onGoogleLogin = {
                    //we do later
                }
            )
        }

        composable(Screen.SignUp.route){
            SignUpScreen(onSignUpSuccess = {
                navHostController.navigate(Screen.Login.route)
            },
                onLoginClick = {
                    navHostController.popBackStack()
                })
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

            }, onEmergencyClick = {

            })

        }
        composable(Screen.DonationHistoryScreen.route) {
            DonationHistoryScreen (
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

//        composable(
//            route = Screen.FindDonorScreen.route,
//            arguments = listOf(
//                navArgument("bloodGroup") { type = NavType.StringType },
//                navArgument("city") { type = NavType.StringType },
//                navArgument("pincode") {
//                    type = NavType.StringType
//                    nullable = true
//                    defaultValue = null
//                },
//                navArgument("state") {
//                    type = NavType.StringType
//                    nullable = true
//                    defaultValue = null
//                }
//            )
//        ) { backStackEntry ->
//
//            val bloodGroup = backStackEntry.arguments?.getString("bloodGroup")!!
//            val city = backStackEntry.arguments?.getString("city")!!
//            val pincode = backStackEntry.arguments?.getString("pincode")
//            val state = backStackEntry.arguments?.getString("state")
//
//            FindDonorScreen(
//                query = FindDonorQuery(
//                    bloodGroup = bloodGroup,
//                    city = city,
//                    pincode = pincode,
//                    state = state
//                ),
//                onBack = { navHostController.popBackStack() }
//            )
//        }



    }
}

