package com.example.redhope.ui.theme.ui

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
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
import com.example.redhope.common.BloodGroupDropdown

import com.example.redhope.viewModel.AuthViewModel
import com.example.redhope.viewModel.ProfileViewModel

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ProfileScreen(
    onProfileSaved: () -> Unit,
    onBackClick:()-> Unit
){

    val viewModel : ProfileViewModel = viewModel()

    val state by viewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()

    var isEditing by remember { mutableStateOf(false) }


    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.loadProfile()
    }

    Box(
        modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.primary)
    ){
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 40.dp, start = 16.dp, end = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Back button
            IconButton(onClick = { onBackClick() }) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White
                )
            }

            Text(
                "My Profile",
                color = Color.White,
                style = MaterialTheme.typography.headlineSmall
            )

            IconButton(
                onClick = {
                    isEditing = true
                }
            ) {
                Icon(
                    imageVector = if (isEditing) Icons.Default.Check else Icons.Default.Edit,
                    contentDescription = if (isEditing) "Save" else "Edit",
                    tint = Color.White
                )
            }
        }


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
                    .padding(20.dp)
                    .fillMaxHeight()
                    .verticalScroll(scrollState)
                    .imePadding(),
                verticalArrangement = Arrangement.spacedBy(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                AuthenticationTextField(
                    value = state.fullName,
                    onValueChange = {viewModel.onFieldChange("fullName",it)},
                    label = "Name",
                    isPassword = false,
                    leadingIcon = R.drawable.user,
                    error = state.fullNameError,
                    enabled = isEditing
                )


                BloodGroupDropdown(
                    selectedBloodGroup = state.bloodGroup,
                    onSelected = {viewModel.onFieldChange("bloodGroup",it)},
                    enabled = isEditing
                )



                AuthenticationTextField(
                    value = state.phone,
                    onValueChange = {viewModel.onFieldChange("phone",it)},
                    label = "Phone Number",
                    isPassword = false,
                    leadingIcon = R.drawable.phone_call,
                    error = state.phoneError,
                    enabled = isEditing
                )

                AuthenticationTextField(
                    value = state.city,
                    onValueChange = {viewModel.onFieldChange("city",it)},
                    label = "City",
                    isPassword = false,
                    leadingIcon = R.drawable.cityscape,
                    error = state.cityError,
                    enabled = isEditing
                )

                AuthenticationTextField(
                    value = state.pincode,
                    onValueChange = {viewModel.onFieldChange("pincode",it)},
                    label = "Pincode",
                    isPassword = false,
                    leadingIcon = R.drawable.pin,
                    error = state.pincodeError,
                    enabled = isEditing
                )



                if(isEditing){
                    AuthenticationButton(
                        onClick = {viewModel.saveProfile(onSuccess = { onProfileSaved() },onFailure = {
                                errorMessage ->
                            Toast.makeText(context,errorMessage, Toast.LENGTH_SHORT).show()
                            Log.e("save",errorMessage)
                        })},
                        text = "Save"
                    )
                }

            }

        }

    }

}




