package com.example.redhope.common

import android.R.attr.name
import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.redhope.modal.DonorUIModel
import com.example.redhope.util.callPhoneNumber
import com.example.redhope.util.getMinutesAgo
import java.nio.file.WatchEvent

@Composable
fun DonorCard(donor: DonorUIModel, onRequestClick: () -> Unit) {

    val context = LocalContext.current
    Log.d("DONOR", donor.locationUpdatedAt.toString())

    Card(
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        modifier = Modifier.fillMaxWidth()
    ) {

            Row(
                modifier = Modifier.padding(16.dp)
            ) {


                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {

                    Text(
                        text = donor.name,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )

                    Row(
                        modifier = Modifier.clickable {
                            callPhoneNumber(context, donor.phone)
                        },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = donor.phone,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Medium
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        Icon(
                            imageVector = Icons.Default.Call,
                            contentDescription = "Call",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    Text(
                        text = "${String.format("%.2f", donor.distanceKm)} km away",
                        color = MaterialTheme.colorScheme.primary
                    )

                    Text(
                        text = "Updated ${getMinutesAgo(donor.locationUpdatedAt)}"
                    )
                }


                Spacer(modifier = Modifier.width(8.dp))


                Column(
                    horizontalAlignment = Alignment.End,
                    verticalArrangement = Arrangement.SpaceBetween
                ) {

                    Text(
                        text = donor.bloodGroup,
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Button(
                        onClick = { onRequestClick() },
                        modifier = Modifier
                            .padding(top = 8.dp)
                    ) {
                        Text("Request")
                    }
                }
            }
        }
    }




//@Preview(showBackground = true)
//@Composable
//fun DonorPreview(){
//    DonorCard(donor = DonorUIModel("1112","Rahul", "O+","7895024033", distanceKm = 5.00))
//}