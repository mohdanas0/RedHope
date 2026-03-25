package com.example.redhope.ui.theme.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.redhope.modal.EligibilityForm
import com.example.redhope.modal.EligibilityResult
import com.example.redhope.util.checkEligibility

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EligibilityPopup(
    onDismiss: () -> Unit,
    onEligible: () -> Unit
) {
    var age by remember { mutableStateOf("") }
    var weight by remember { mutableStateOf("") }
    var lastDonationMonths by remember { mutableStateOf("") }

    var hasIllness by remember { mutableStateOf(false) }
    var hasRecentSurgery by remember { mutableStateOf(false) }
    var onMedication by remember { mutableStateOf(false) }

    var errorMessage by remember { mutableStateOf<String?>(null) }

    Dialog (
        onDismissRequest = onDismiss,

    ) {
        Card(
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth()
        ){
            Column(
                modifier = Modifier
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {

                Text(
                    text = "Eligibility Check",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )

                OutlinedTextField(
                    value = age,
                    onValueChange = { age = it.filter { c -> c.isDigit() } },
                    label = { Text("Age") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = weight,
                    onValueChange = { weight = it.filter { c -> c.isDigit() } },
                    label = { Text("Weight (kg)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = lastDonationMonths,
                    onValueChange = { lastDonationMonths = it.filter { c -> c.isDigit() } },
                    label = { Text("Months since last donation") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )

                // Switches
                EligibilitySwitch("Any illness in last 6 months?", hasIllness) { hasIllness = it }
                EligibilitySwitch("Any surgery in last 6 months?", hasRecentSurgery) { hasRecentSurgery = it }
                EligibilitySwitch("Currently on medication?", onMedication) { onMedication = it }

                errorMessage?.let {
                    Text(text = it, color = Color.Red, fontSize = 14.sp)
                }

                Button(
                    onClick = {
                        if (age.isBlank() || weight.isBlank() || lastDonationMonths.isBlank()) {
                            errorMessage = "Please fill all required fields"
                            return@Button
                        }

                        val form = EligibilityForm(
                            age = age.toInt(),
                            weight = weight.toInt(),
                            lastDonationMonthsAgo = lastDonationMonths.toInt(),
                            hasIllness = hasIllness,
                            hasRecentSurgery = hasRecentSurgery,
                            onMedication = onMedication
                        )

                        when (val result = checkEligibility(form)) {
                            is EligibilityResult.Eligible -> {
                                onEligible()
                                onDismiss()
                            }

                            is EligibilityResult.NotEligible -> {
                                errorMessage = result.reason
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Check Eligibility")
                }

                Spacer(modifier = Modifier.height(10.dp))
            }
        }

    }
}
