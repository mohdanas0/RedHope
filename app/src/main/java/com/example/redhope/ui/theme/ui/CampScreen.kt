package com.example.redhope.ui.theme.ui



import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

import com.example.redhope.viewModel.CampViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CampScreen(onBack: () -> Unit) {

    val viewModel: CampViewModel = viewModel()

    Scaffold(

        topBar = {

            androidx.compose.material3.TopAppBar(

                title = {

                    Text(
                        text = "Blood Donation Camps"
                    )
                },
                        navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = null)
                    }
                }
            )
        }

    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {

            Text(
                text = "Upcoming Camps",
                style = MaterialTheme.typography.headlineSmall
            )

            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn {

                items(viewModel.campList) { camp ->

                    BloodCampCard(
                        camp = camp
                    )
                }
            }
        }
    }
}