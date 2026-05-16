package com.example.redhope.ui.theme.ui



import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.redhope.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BloodCompatibilityScreen(
    onBack: () -> Unit
) {

    val bloodGroups = listOf(
        "O+", "O-",
        "A+", "A-",
        "B+", "B-",
        "AB+", "AB-"
    )

    //Donate to
    val donateMap = mapOf(

        "O-" to listOf("O-", "O+", "A-", "A+", "B-", "B+", "AB-", "AB+"),

        "O+" to listOf("O+", "A+", "B+", "AB+"),

        "A-" to listOf("A-", "A+", "AB-", "AB+"),

        "A+" to listOf("A+", "AB+"),

        "B-" to listOf("B-", "B+", "AB-", "AB+"),

        "B+" to listOf("B+", "AB+"),

        "AB-" to listOf("AB-", "AB+"),

        "AB+" to listOf("AB+")
    )

    // RECEIVE FROM
    val receiveMap = mapOf(

        "O-" to listOf("O-"),

        "O+" to listOf("O+", "O-"),

        "A-" to listOf("A-", "O-"),

        "A+" to listOf("A+", "A-", "O+", "O-"),

        "B-" to listOf("B-", "O-"),

        "B+" to listOf("B+", "B-", "O+", "O-"),

        "AB-" to listOf("AB-", "A-", "B-", "O-"),

        "AB+" to listOf(
            "O-", "O+",
            "A-", "A+",
            "B-", "B+",
            "AB-", "AB+"
        )
    )

    var selectedGroup by remember {
        mutableStateOf("O+")
    }

    val donateList = donateMap[selectedGroup] ?: emptyList()
    val receiveList = receiveMap[selectedGroup] ?: emptyList()

    Scaffold(

        topBar = {

            TopAppBar(

                title = {
                    Text(
                        "Blood Compatibility",
                        fontWeight = FontWeight.Bold
                    )
                },

                navigationIcon = {

                    IconButton(
                        onClick = onBack
                    ) {

                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = null
                        )
                    }
                }
            )
        }

    ) { padding ->

        Column(

            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.background)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {



            Card(

                modifier = Modifier.fillMaxWidth(),

                shape = RoundedCornerShape(24.dp),

                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.background
                ),

                elevation = CardDefaults.cardElevation(6.dp)
            ) {

                Row(

                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),

                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Image(
                        painter = painterResource(R.drawable.bloodcompatibility),
                        contentDescription = null,
                        modifier = Modifier.size(80.dp)
                    )

                    Spacer(modifier = Modifier.width(16.dp))

                    Column {

                        Text(
                            text = "Check Blood Donation Compatibility",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = "Select blood group to check donation and receiving compatibility.",
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                            fontSize = 13.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Select Blood Group",
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp
            )

            Spacer(modifier = Modifier.height(16.dp))



            LazyVerticalGrid(

                columns = GridCells.Fixed(4),

                modifier = Modifier.height(130.dp),

                verticalArrangement = Arrangement.spacedBy(12.dp),

                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                items(bloodGroups) { group ->

                    Card(

                        modifier = Modifier
                            .fillMaxWidth()
                            .height(55.dp)
                            .clickable {
                                selectedGroup = group
                            },

                        shape = RoundedCornerShape(16.dp),

                        colors = CardDefaults.cardColors(

                            containerColor =
                                if (selectedGroup == group)
                                    MaterialTheme.colorScheme.primary
                                else
                                    MaterialTheme.colorScheme.background
                        ),

                        elevation = CardDefaults.cardElevation(5.dp)
                    ) {

                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {

                            Text(
                                text = group,

                                color =
                                    if (selectedGroup == group)
                                        Color.White
                                    else
                                        MaterialTheme.colorScheme.onBackground,

                                fontWeight = FontWeight.Bold,
                                fontSize = 20.sp
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(26.dp))



            CompatibilitySection(
                title = "$selectedGroup can donate to",
                list = donateList
            )

            Spacer(modifier = Modifier.height(24.dp))


            CompatibilitySection(
                title = "$selectedGroup can receive from",
                list = receiveList
            )

            Spacer(modifier = Modifier.height(24.dp))



            Card(

                modifier = Modifier.fillMaxWidth(),

                shape = RoundedCornerShape(20.dp),

                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.background
                ),

                elevation = CardDefaults.cardElevation(5.dp)
            ) {

                Column(
                    modifier = Modifier.padding(18.dp)
                ) {

                    Text(
                        text = "Important Note",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "This information is for blood donation compatibility only. For receiving blood please consult a medical professional.",
                        fontSize = 14.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}

@Composable
fun CompatibilitySection(
    title: String,
    list: List<String>
) {

    Text(
        text = title,
        fontWeight = FontWeight.Bold,
        fontSize = 22.sp,
        color = MaterialTheme.colorScheme.primary
    )

    Spacer(modifier = Modifier.height(16.dp))

    list.forEach { group ->

        Card(

            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 7.dp),

            shape = RoundedCornerShape(18.dp),

            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.background
            ),

            elevation = CardDefaults.cardElevation(5.dp)
        ) {

            Row(

                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),

                verticalAlignment = Alignment.CenterVertically,

                horizontalArrangement = Arrangement.SpaceBetween
            ) {

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Box(

                        modifier = Modifier
                            .size(50.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary),

                        contentAlignment = Alignment.Center
                    ) {

                        Text(
                            text = group,
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column {

                        Text(
                            text = group,
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp
                        )

                        Text(
                            text = "Compatible",
                            color = Color(0xFF4CAF50)
                        )
                    }
                }

                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = Color(0xFF4CAF50),
                    modifier = Modifier.size(32.dp)
                )
            }
        }
    }
}