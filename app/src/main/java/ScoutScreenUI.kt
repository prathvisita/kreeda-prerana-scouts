package com.example.kreedapreranascouts

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BeautifulScoutScreen(
    viewModel: ScoutViewModel = viewModel()
) {

    val scouts by viewModel.scouts.collectAsState(initial = emptyList())

    var name by remember { mutableStateOf("") }
    var age by remember { mutableStateOf("") }
    var district by remember { mutableStateOf("") }
    var level by remember { mutableStateOf("") }

    var search by remember { mutableStateOf("") }

    val filtered = scouts.filter {
        it.name.contains(search, true)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color(0xFF0F172A),
                        Color(0xFF1E293B),
                        Color(0xFF334155)
                    )
                )
            )
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {

            Text(
                text = "Kreeda Prerana",
                color = Color.White,
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "Scout Management System",
                color = Color.LightGray,
                fontSize = 16.sp
            )

            Spacer(modifier = Modifier.height(20.dp))

            Card(
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                ),
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier.fillMaxWidth()
            ) {

                Column(
                    modifier = Modifier.padding(18.dp)
                ) {

                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Scout Name") },
                        modifier = Modifier.fillMaxWidth(),
                        leadingIcon = {
                            Icon(Icons.Default.Person, null)
                        }
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = age,
                        onValueChange = { age = it },
                        label = { Text("Age") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = district,
                        onValueChange = { district = it },
                        label = { Text("District") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = level,
                        onValueChange = { level = it },
                        label = { Text("Level") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(18.dp))

                    Button(
                        onClick = {

                            if (
                                name.isNotEmpty() &&
                                age.isNotEmpty() &&
                                district.isNotEmpty() &&
                                level.isNotEmpty()
                            ) {

                                viewModel.addScout(
                                    name,
                                    age.toInt(),
                                    district,
                                    level
                                )

                                name = ""
                                age = ""
                                district = ""
                                level = ""
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(55.dp),
                        shape = RoundedCornerShape(16.dp)
                    ) {

                        Text(
                            "Add Scout",
                            fontSize = 18.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            OutlinedTextField(
                value = search,
                onValueChange = { search = it },
                label = { Text("Search Scouts") },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White
                )
            )

            Spacer(modifier = Modifier.height(20.dp))

            LazyColumn {

                items(filtered) { scout ->

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 14.dp),
                        shape = RoundedCornerShape(22.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color.White
                        )
                    ) {

                        Column(
                            modifier = Modifier.padding(18.dp)
                        ) {

                            Text(
                                text = scout.name,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF0F172A)
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            Text("Age: ${scout.age}")
                            Text("District: ${scout.district}")
                            Text("Level: ${scout.level}")

                            Spacer(modifier = Modifier.height(14.dp))

                            Row(
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {

                                FilledTonalButton(
                                    onClick = {
                                        viewModel.deleteScout(scout)
                                    }
                                ) {

                                    Icon(Icons.Default.Delete, null)

                                    Spacer(
                                        modifier = Modifier.width(6.dp)
                                    )

                                    Text("Delete")
                                }

                                FilledTonalButton(
                                    onClick = {

                                        name = scout.name
                                        age = scout.age.toString()
                                        district = scout.district
                                        level = scout.level
                                    }
                                ) {

                                    Icon(Icons.Default.Edit, null)

                                    Spacer(
                                        modifier = Modifier.width(6.dp)
                                    )

                                    Text("Edit")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
