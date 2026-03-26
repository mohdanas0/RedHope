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
import com.example.redhope.modal.DonationHistory
import com.example.redhope.modal.DonorUIModel
import com.example.redhope.util.callPhoneNumber
import com.example.redhope.util.getMinutesAgo
import java.nio.file.WatchEvent

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




