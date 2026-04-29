package com.example.redhope.common



import androidx.compose.foundation.layout.Column

import androidx.compose.foundation.layout.padding

import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

import androidx.compose.ui.Modifier

import androidx.compose.ui.text.font.FontWeight

import androidx.compose.ui.unit.dp
import com.example.redhope.modal.DonationHistory


@Composable
fun HistoryCard(history: DonationHistory) {

    Card(
        elevation = CardDefaults.cardElevation(4.dp)
    ) {

        Column(modifier = Modifier.padding(12.dp)) {

            Text(
                "Donated to ${history.receiverName}",
                fontWeight = FontWeight.Bold
            )

            Text("Blood Group: ${history.bloodGroup}")

            history.completedAt?.let {
                Text("Date: ${it.toDate()}")
            }
        }
    }
}




