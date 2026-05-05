package com.example.redhope.ui.theme.ui

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.redhope.R
import com.example.redhope.common.AuthenticationButton
import com.example.redhope.viewModel.AuthViewModel

@Composable
fun EmailVerificationScreen(
    onVerified: () -> Unit,
    viewModel: AuthViewModel = viewModel()
) {
    val context = LocalContext.current
    var isChecking by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.primary)
    ) {

        Card (
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 200.dp),
            shape = RoundedCornerShape(topStart = 25.dp, topEnd = 25.dp)
        ) {

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Image(
                    painter = painterResource(R.drawable.email),
                    contentDescription = "Email",
                    modifier = Modifier.size(120.dp)
                )

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = "Verify Your Email",
                    style = MaterialTheme.typography.headlineMedium
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "We’ve sent a verification link to your email.\nPlease check your inbox or spam folder.",
                    fontSize = 14.sp,
                    color = Color.Gray
                )

                Spacer(modifier = Modifier.height(30.dp))

                // ✅ Check verification button
                AuthenticationButton(
                    onClick = {
                        isChecking = true
                        viewModel.checkEmailVerified(
                            onVerified = {
                                isChecking = false
                                onVerified()
                            },
                            onNotVerified = {
                                isChecking = false
                                Toast.makeText(context, "Still not verified", Toast.LENGTH_SHORT).show()
                            }
                        )
                    },
                    text = "I have verified",
                    isLoading = isChecking
                )

                Spacer(modifier = Modifier.height(10.dp))

                // 🔁 Resend email
                Text(
                    text = "Resend Email",
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable {
                        viewModel.resendVerification {
                            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                        }
                    }
                )
            }
        }
    }
}