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
import androidx.compose.foundation.rememberScrollState

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.traceEventEnd
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
fun SignUpScreen(
    onSignUpSuccess : () -> Unit,
    onLoginClick:()->Unit
){
    val viewModel : AuthViewModel = viewModel()

    val state by viewModel.uiState

    val scrollState = rememberScrollState()

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
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
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

                Text(text = "Donate Now", style = MaterialTheme.typography.headlineLarge)

                AuthenticationTextField(
                    value = state.fullName,
                    onValueChange = { viewModel.onFieldChange("fullName",it)},
                    label = "FullName",
                    leadingIcon = R.drawable.user,
                    isPassword = false,
                    error = state.fullNameError
                )



                AuthenticationTextField(
                    value = state.email,
                    onValueChange = {viewModel.onFieldChange("email",it)},
                    label = "Email",
                    leadingIcon = R.drawable.email,
                    isPassword = false,
                    error = state.emailError
                )


                AuthenticationTextField(
                    value = state.password,
                    onValueChange = { viewModel.onFieldChange("password",it)},
                    label = "Password",
                    leadingIcon = R.drawable.lock,
                    isPassword = true,
                    error = state.passwordError
                )

                AuthenticationTextField(
                    value = state.confirmPassword,
                    onValueChange = {viewModel.onFieldChange("confirmPassword",it)},
                    label = "Confirm Password",
                    leadingIcon = R.drawable.lock,
                    isPassword = true,
                    error = state.confirmPasswordError
                )




                Spacer(modifier = Modifier.height(15.dp))


                AuthenticationButton(
                    onClick = {
                        viewModel.signUp(onSuccess = { onSignUpSuccess() }, onFailure = { errorMessage ->
                            Toast.makeText(context,errorMessage, Toast.LENGTH_SHORT).show()
                        }
                            )

                              },
                    text = "SignUp",
                    isLoading = state.isLoading
                )



                Row(
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Already have an account? ", fontSize = 16.sp)

                    Text(
                        text = "Login",
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.clickable { onLoginClick() }
                    )
                }

            }

        }

    }


}


@Preview(showBackground = true)
@Composable
fun SignUpScreenPreview(){
    RedHopeTheme {
       SignUpScreen(
           onSignUpSuccess = {println("Sign Clicked")}, onLoginClick = {println("login clicked")}
       )
    }

}