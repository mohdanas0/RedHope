package com.example.redhope.ui.theme.ui

import android.util.Log
import android.util.Patterns
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.redhope.R
import com.example.redhope.common.AuthenticationButton
import com.example.redhope.common.AuthenticationTextField
import com.example.redhope.ui.theme.RedHopeTheme
import com.example.redhope.viewModel.AuthViewModel
import com.google.firebase.auth.FirebaseAuth


@Composable
fun LoginScreen(
    onLoginClick : () -> Unit,
    onGoogleLogin : () -> Unit,
    onSignUpClick : () -> Unit
){
    val viewModel : AuthViewModel = viewModel()

    val state by viewModel.uiState
    val scrollState = rememberScrollState()

    var showResetDialog by remember { mutableStateOf(false) }
    var resetEmail by remember { mutableStateOf("") }
    val context = LocalContext.current


    Box(
      modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.primary)
    ){

        Card(
            modifier = Modifier
                .fillMaxSize()
                .padding(top=200.dp)
                .align(Alignment.Center),
            shape = RoundedCornerShape(topStart = 25.dp, topEnd = 25.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background),
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .fillMaxHeight()
                    .verticalScroll(scrollState)
                    .imePadding(),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(painter = painterResource(R.drawable.bloodlogo), contentDescription = "logo",
                    Modifier.size(200.dp))

                Text(text = "Donate Now", style = MaterialTheme.typography.headlineLarge, color = MaterialTheme.colorScheme.onBackground)

                AuthenticationTextField(
                    value = state.email,
                    onValueChange = {viewModel.onFieldChange("email",it)},
                    label = "Email",
                    leadingIcon = R.drawable.email,
                    isPassword = false,
                    error = state.emailError,
                )



                AuthenticationTextField(
                    value = state.password,
                    onValueChange = {viewModel.onFieldChange("password",it)},
                    label = "Password",
                    leadingIcon = R.drawable.lock,
                    isPassword = true,
                    error = state.passwordError
                )

                Text(
                    text = "Forgot Password?",
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .align(Alignment.End)
                        .clickable {
                            showResetDialog = true
                        }
                )


                Spacer(modifier = Modifier.height(15.dp))


                AuthenticationButton(
                    onClick = {
                       viewModel.login { onLoginClick() } },
                    text = "Login",

                    isLoading = state.isLoading
                )



                OutlinedButton(
                    onClick = onGoogleLogin,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.google), // google icon
                        contentDescription = "Google Icon",
                        tint = Color.Unspecified,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Continue with Google")
                }


                Row(
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Don't have an account? ", fontSize = 16.sp,color = MaterialTheme.colorScheme.onBackground)

                    Text(
                        text = "Sign Up",
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.clickable { onSignUpClick() }
                    )
                }

            }

        }

    }
    if(showResetDialog){
        AlertDialog(
            onDismissRequest = { showResetDialog = false },
            confirmButton = {
                TextButton(onClick = {

                    FirebaseAuth.getInstance()
                        .sendPasswordResetEmail(resetEmail)
                        .addOnSuccessListener {
                            Toast.makeText(
                                context,
                                "Reset link sent to email",
                                Toast.LENGTH_LONG
                            ).show()
                            showResetDialog = false
                        }
                        .addOnFailureListener {
                            Toast.makeText(
                                context,
                                "Error: ${it.message}",
                                Toast.LENGTH_LONG
                            ).show()
                        }

                }) {
                    Text("Send")
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetDialog = false }) {
                    Text("Cancel")
                }
            },
            title = { Text("Reset Password") },
            text = {
                Column {
                    Text("Enter your registered email")
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = resetEmail,
                        onValueChange = { resetEmail = it },
                        label = { Text("Email") }
                    )
                }
            }
        )
    }


}


@Preview(showBackground = true)
@Composable
fun LoginScreenPreview(){
    RedHopeTheme {
        LoginScreen( onLoginClick = {
            // Just print dummy values in Preview (won’t actually run)
            println("Login clicked")
        }, onGoogleLogin = {println("Google clicked")}, onSignUpClick = {println("Signup click")})
    }

}