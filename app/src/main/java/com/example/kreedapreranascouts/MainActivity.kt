package com.example.kreedapreranascouts

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.foundation.clickable
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.ui.layout.ContentScale
import coil.compose.rememberAsyncImagePainter

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                ScoutScreen()
            }
        }
    }
}

@Composable
fun ScoutScreen(
    viewModel: ScoutViewModel = viewModel()
) {
    val scouts by viewModel.scouts.collectAsState(initial = emptyList())

    var searchText by remember { mutableStateOf("") }
    var selectedTab by remember { mutableStateOf("home") }
    var isLoggedIn by remember { mutableStateOf(false) }
    var selectedScout by remember { mutableStateOf<Scout?>(null) }
    var teacherName by remember { mutableStateOf("PE Teacher") }
    var teacherAge by remember { mutableStateOf("") }
    var teacherGender by remember { mutableStateOf("") }
    var teacherDistrict by remember { mutableStateOf("") }
    var teacherMobile by remember { mutableStateOf("") }
    var teacherEmail by remember { mutableStateOf("") }
    var teacherImageUri by remember { mutableStateOf<String?>(null) }
    val filteredScouts = scouts.filter {
        it.name.contains(searchText, ignoreCase = true)
    }

    val darkBg = Color(0xFF070D1D)

    Scaffold(
        containerColor = darkBg,
        bottomBar = {
            if (isLoggedIn) {
                NavigationBar(
                    containerColor = Color(0xFF0B1224)
                ) {
                    NavigationBarItem(
                        selected = selectedTab == "home",
                        onClick = { selectedTab = "home" },
                        icon = { Text("🏠", fontSize = 22.sp) },
                        label = { Text("Home") }
                    )

                    NavigationBarItem(
                        selected = selectedTab == "events",
                        onClick = { selectedTab = "events" },
                        icon = { Text("⏱", fontSize = 22.sp) },
                        label = { Text("Timer") }
                    )

                    NavigationBarItem(
                        selected = selectedTab == "batch",
                        onClick = { selectedTab = "batch" },
                        icon = { Text("👥", fontSize = 22.sp) },
                        label = { Text("Batch") }
                    )

                    NavigationBarItem(
                        selected = selectedTab == "leaderboard",
                        onClick = { selectedTab = "leaderboard" },
                        icon = { Text("🏆", fontSize = 22.sp) },
                        label = { Text("Leaderboard") }
                    )
                }
            }
        }
    ) { padding ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(darkBg)
        ) {
            if (!isLoggedIn) {

                LoginScreen(
                    onLoginSuccess = { name, age, gender, district, mobile, email ->
                        teacherName = name
                        teacherAge = age
                        teacherGender = gender
                        teacherDistrict = district
                        teacherMobile = mobile
                        teacherEmail = email
                        isLoggedIn = true
                    }
                )

            } else if (selectedScout != null) {

                AthleteProfileScreen(
                    scout = selectedScout!!,
                    viewModel = viewModel,
                    onBack = { selectedScout = null }
                )

            } else {

                when (selectedTab) {

                    "home" -> {
                        HomeScreen(
                            scouts = scouts,
                            filteredScouts = filteredScouts,
                            searchText = searchText,
                            onSearchChange = { searchText = it },
                            viewModel = viewModel,
                            onScoutClick = { selectedScout = it },
                            onProfileClick = { selectedTab = "auth" },
                            onViewAllClick = { selectedTab = "leaderboard" }
                        )
                    }

                    "auth" -> {
                        AuthScreen(
                            name = teacherName,
                            age = teacherAge,
                            gender = teacherGender,
                            district = teacherDistrict,
                            mobile = teacherMobile,
                            email = teacherEmail,
                            imageUri = teacherImageUri,
                            onImageChange = { teacherImageUri = it },
                            onSave = { name, age, gender, district, mobile, email ->
                                teacherName = name
                                teacherAge = age
                                teacherGender = gender
                                teacherDistrict = district
                                teacherMobile = mobile
                                teacherEmail = email
                            },
                            onLogout = {
                                isLoggedIn = false
                                selectedTab = "home"
                                selectedScout = null
                            }
                        )
                    }

                    "events" -> {
                        EventsScreen()
                    }

                    "batch" -> {
                        BatchEntryScreen(viewModel)
                    }

                    "leaderboard" -> {
                        LeaderboardScreen(viewModel)
                    }
                }
            }
        }
    }
}
    @Composable
    fun HomeScreen(
        scouts: List<Scout>,
        filteredScouts: List<Scout>,
        searchText: String,
        onSearchChange: (String) -> Unit,
        viewModel: ScoutViewModel,
        onScoutClick: (Scout) -> Unit,
        onProfileClick: () -> Unit,
        onViewAllClick: () -> Unit
    ) {
        val cardBg = Color(0xFF1B2638)
        val orange = Color(0xFFFFA31A)
        var showAll by remember { mutableStateOf(false) }

        val visibleScouts =
            if (showAll) filteredScouts
            else filteredScouts.take(3)
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp, vertical = 20.dp),

            contentPadding = PaddingValues(bottom = 100.dp),

            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            "Kreeda Prerna",
                            color = Color.White,
                            fontSize = 30.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Text(
                            "Discover. Train. Excel.",
                            color = Color.Gray,
                            fontSize = 16.sp
                        )
                    }

                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .background(
                                brush = Brush.linearGradient(
                                    listOf(Color(0xFFFFA31A), Color(0xFFFF5E3A))
                                ),
                                shape = CircleShape
                            )
                            .clickable {
                                onProfileClick()
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text("👤", fontSize = 26.sp)
                    }
                }
            }

            item {
                OutlinedTextField(
                    value = searchText,
                    onValueChange = onSearchChange,
                    placeholder = { Text("Search athletes, sports...") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = cardBg,
                        unfocusedContainerColor = cardBg,
                        focusedBorderColor = cardBg,
                        unfocusedBorderColor = cardBg,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    )
                )
            }

            item {
                Row(
                    modifier = Modifier.horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    SportChip("All", true)
                    SportChip("🏏 Cricket", false)
                    SportChip("⚽ Football", false)
                    SportChip("🏃 Athletics", false)
                    SportChip("🤼 Kabaddi", false)
                }
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = cardBg)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(22.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        StatItem("${scouts.size}", "Athletes")
                        StatItem("12", "Districts")
                        StatItem("8", "Sports")
                    }
                }
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        "Top Prospects",
                        color = Color.White,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        if (showAll) "Show Less" else "View All",
                        color = orange,
                        fontSize = 16.sp,
                        modifier = Modifier.clickable {
                            showAll = !showAll
                        }
                    )
                }
            }

            items(
                visibleScouts.sortedBy { it.age }
            ) { scout ->

                val score = when {
                    scout.age <= 12 -> 95
                    scout.age <= 14 -> 90
                    scout.age <= 16 -> 85
                    else -> 80
                }

                Box(
                    modifier = Modifier.clickable {
                        onScoutClick(scout)
                    }
                ) {
                    ProspectCard(
                        scout = scout,
                        score = score,
                        onEdit = {},
                        onDelete = {
                            viewModel.deleteScout(scout)
                        },
                        onAttendance = {
                            viewModel.toggleAttendance(scout)
                        }
                    )
                }
            }
        }
    }



@Composable
fun SportChip(
    text: String,
    selected: Boolean
) {
    Box(
        modifier = Modifier
            .background(
                color = if (selected) Color(0xFFFFA31A) else Color(0xFF1B2638),
                shape = RoundedCornerShape(24.dp)
            )
            .padding(horizontal = 14.dp, vertical = 8.dp)
    ) {
        Text(
            text = text,
            color = if (selected) Color.Black else Color.White,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun StatItem(
    number: String,
    label: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            number,
            color = Color.White,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold
        )

        Text(
            label,
            color = Color.Gray,
            fontSize = 12.sp
        )
    }
}

@Composable
fun ProspectCard(
    scout: Scout,
    score: Int,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onAttendance: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF1B2638)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .background(
                        brush = Brush.linearGradient(
                            listOf(Color(0xFF6366F1), Color(0xFF3B82F6))
                        ),
                        shape = RoundedCornerShape(18.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text("🏃", fontSize = 30.sp)
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    scout.name,
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    "${scout.level} • ${scout.district} • Age ${scout.age}",
                    color = Color.Gray,
                    fontSize = 14.sp
                )

                Text(
                    if (scout.attendance) "Present" else "Absent",
                    color = if (scout.attendance) Color(0xFF10B981) else Color(0xFFFF5252),
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = if (score >= 90)
                        "🏅 District Level Ready"
                    else
                        "Training Stage",
                    color = if (score >= 90)
                        Color(0xFFFFA31A)
                    else
                        Color.Gray,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(6.dp))

                Button(
                    onClick = {
                        onDelete()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFFF5252)
                    ),
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = PaddingValues(
                        horizontal = 12.dp,
                        vertical = 4.dp
                    )
                ) {
                    Text(
                        "Delete",
                        color = Color.White,
                        fontSize = 12.sp
                    )
                }
            }


            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    "$score",
                    color = Color(0xFFFFA31A),
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    "Score",
                    color = Color.Gray,
                    fontSize = 12.sp
                )
            }
        }
    }
}
@Composable
fun CenterText(text: String) {

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {

        Text(
            text = text,
            color = Color.White,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )
    }
}
@Composable
fun EventsScreen(
    viewModel: ScoutViewModel = viewModel()
) {
    val scouts by viewModel.scouts.collectAsState(initial = emptyList())

    var selectedScout by remember { mutableStateOf<Scout?>(null) }

    var time by remember { mutableStateOf(0L) }
    var isRunning by remember { mutableStateOf(false) }
    var startTime by remember { mutableStateOf(0L) }

    LaunchedEffect(isRunning) {
        if (isRunning) {
            startTime = System.currentTimeMillis() - time

            while (isRunning) {
                time = System.currentTimeMillis() - startTime
                kotlinx.coroutines.delay(10)
            }
        }
    }

    val seconds = time / 1000.0

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {

        item {
            Text(
                "⏱ Sprint Timer",
                color = Color.White,
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold
            )
        }

        item {
            Text(
                text = String.format("%.2f sec", seconds),
                color = Color(0xFFFFA31A),
                fontSize = 48.sp,
                fontWeight = FontWeight.Bold
            )
        }

        item {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(onClick = { isRunning = true }) {
                    Text("Start")
                }

                Button(onClick = { isRunning = false }) {
                    Text("Stop")
                }

                Button(
                    onClick = {
                        isRunning = false
                        time = 0L
                    }
                ) {
                    Text("Reset")
                }
            }
        }

        item {
            Text(
                "Select Athlete",
                color = Color.White,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )
        }

        items(scouts) { scout ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(
                    containerColor =
                        if (selectedScout?.id == scout.id)
                            Color(0xFFFFA31A)
                        else
                            Color(0xFF1B2638)
                )
            ) {
                Text(
                    text = scout.name,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .clickable {
                            selectedScout = scout
                        },
                    color =
                        if (selectedScout?.id == scout.id)
                            Color.Black
                        else
                            Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        item {
            Button(
                onClick = {
                    selectedScout?.let { scout ->
                        viewModel.addPerformance(
                            scoutId = scout.id,
                            eventName = "100m Sprint",
                            timeSeconds = seconds,
                            date = SimpleDateFormat(
                                "dd MMM yyyy",
                                Locale.getDefault()
                            ).format(Date())
                        )

                        isRunning = false
                        time = 0L
                    }
                },
                enabled = selectedScout != null && time > 0L,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(18.dp)
            ) {
                Text("Save Sprint Time")
            }
        }
    }
}
@Composable
fun AddScoutScreen(
    viewModel: ScoutViewModel
) {
    var name by remember { mutableStateOf("") }
    var age by remember { mutableStateOf("") }
    var district by remember { mutableStateOf("") }
    var level by remember { mutableStateOf("") }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Text(
                "➕ Add Athlete",
                color = Color.White,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            )
        }

        item {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Athlete Name") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),

                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color(0xFF1B2638),
                    unfocusedContainerColor = Color(0xFF1B2638),

                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,

                    focusedBorderColor = Color(0xFFFFA31A),
                    unfocusedBorderColor = Color.Gray,

                    focusedLabelColor = Color(0xFFFFA31A),
                    unfocusedLabelColor = Color.Gray,

                    cursorColor = Color.White
                )
            )
        }

        item {
            OutlinedTextField(
                value = age,
                onValueChange = { age = it },
                label = { Text("Age") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),

                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color(0xFF1B2638),
                    unfocusedContainerColor = Color(0xFF1B2638),

                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,

                    focusedBorderColor = Color(0xFFFFA31A),
                    unfocusedBorderColor = Color.Gray,

                    focusedLabelColor = Color(0xFFFFA31A),
                    unfocusedLabelColor = Color.Gray,

                    cursorColor = Color.White
                )
            )
        }

        item {
            OutlinedTextField(
                value = district,
                onValueChange = { district = it },
                label = { Text("District") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),

                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color(0xFF1B2638),
                    unfocusedContainerColor = Color(0xFF1B2638),

                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,

                    focusedBorderColor = Color(0xFFFFA31A),
                    unfocusedBorderColor = Color.Gray,

                    focusedLabelColor = Color(0xFFFFA31A),
                    unfocusedLabelColor = Color.Gray,

                    cursorColor = Color.White
                )
            )
        }

        item {
            OutlinedTextField(
                value = level,
                onValueChange = { level = it },
                label = { Text("Sport / Level") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),

                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color(0xFF1B2638),
                    unfocusedContainerColor = Color(0xFF1B2638),

                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,

                    focusedBorderColor = Color(0xFFFFA31A),
                    unfocusedBorderColor = Color.Gray,

                    focusedLabelColor = Color(0xFFFFA31A),
                    unfocusedLabelColor = Color.Gray,

                    cursorColor = Color.White
                )
            )
        }

        item {
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
                    .height(56.dp),
                shape = RoundedCornerShape(18.dp)
            ) {
                Text("Save Athlete")
            }
        }
    }
}

@Composable
fun ProfileScreen() {
    CenterText("👤 Teacher Profile")
}

@Composable
fun AthleteProfileScreen(
    scout: Scout,
    viewModel: ScoutViewModel = viewModel(),
    onBack: () -> Unit
) {
    val performances by viewModel
        .getPerformancesForScout(scout.id)
        .collectAsState(initial = emptyList())

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        item {
            Button(
                onClick = onBack,
                shape = RoundedCornerShape(14.dp)
            ) {
                Text("← Back")
            }
        }

        item {
            Text(
                scout.name,
                color = Color.White,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold
            )

            Text(
                "${scout.level} • ${scout.district}",
                color = Color.Gray,
                fontSize = 16.sp
            )
        }

        item {
            Text(
                "Sprint History",
                color = Color.White,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
        }
        item {

            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF1B2638)
                )
            ) {

                Column(
                    modifier = Modifier.padding(18.dp)
                ) {

                    Text(
                        "Performance Level",
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    LinearProgressIndicator(
                        progress = if (performances.isNotEmpty()) 0.85f else 0.25f,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(12.dp),

                        color = Color(0xFFFFA31A),
                        trackColor = Color.DarkGray
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        if (performances.isNotEmpty())
                            "85% Talent Growth"
                        else
                            "25% Talent Growth — No sprint records yet",
                        color = Color.Gray
                    )
                }
            }
        }

        if (performances.isEmpty()) {
            item {
                Text(
                    "No sprint records yet",
                    color = Color.Gray,
                    fontSize = 16.sp
                )
            }
        } else {
            items(performances) { performance ->
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFF1B2638)
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            performance.eventName,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )

                        Text(
                            "${performance.timeSeconds} sec",
                            color = Color(0xFFFFA31A),
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Text(
                            performance.date,
                            color = Color.Gray
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun LeaderboardScreen(
    viewModel: ScoutViewModel = viewModel()
) {

    val performances by viewModel
        .getLeaderboard()
        .collectAsState(initial = emptyList())
    val scouts by viewModel.scouts.collectAsState(initial = emptyList())

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),

        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        item {

            Text(
                "🏆 Sprint Leaderboard",
                color = Color.White,
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold
            )
        }

        itemsIndexed(performances) { index, performance ->

            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF1B2638)
                )
            ) {

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),

                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        Text(
                            "#${index + 1}",
                            color = Color(0xFFFFA31A),
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.width(16.dp))

                        Column {

                            Text(
                                getScoutNameById(performance.scoutId, scouts),
                                color = Color.White,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )

                            Text(
                                performance.date,
                                color = Color.Gray
                            )
                        }
                    }

                    Text(
                        "${performance.timeSeconds} sec",
                        color = Color(0xFF10B981),
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
fun getScoutNameById(
    scoutId: Int,
    scouts: List<Scout>
): String {
    return scouts.find { it.id == scoutId }?.name ?: "Unknown Athlete"
}
@Composable
fun BatchEntryScreen(
    viewModel: ScoutViewModel
) {

    var batchText by remember { mutableStateOf("") }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),

        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        item {

            Text(
                "👥 Batch Entry",
                color = Color.White,
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold
            )
        }

        item {

            Text(
                "Format:\nName,Age,District,Sport",
                color = Color.Gray
            )
        }

        item {

            OutlinedTextField(
                value = batchText,
                onValueChange = { batchText = it },

                label = {
                    Text("Enter Athletes")
                },

                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp),

                shape = RoundedCornerShape(18.dp),

                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,

                    focusedContainerColor = Color(0xFF1B2638),
                    unfocusedContainerColor = Color(0xFF1B2638),

                    focusedBorderColor = Color(0xFFFFA31A),
                    unfocusedBorderColor = Color.Gray,

                    focusedLabelColor = Color(0xFFFFA31A),
                    unfocusedLabelColor = Color.Gray,

                    cursorColor = Color.White
                )
            )
        }

        item {

            Button(
                onClick = {

                    val lines = batchText.lines()

                    lines.forEach { line ->

                        val parts = line.split(",")

                        if (parts.size == 4) {

                            val name = parts[0].trim()
                            val age = parts[1].trim().toIntOrNull() ?: 12
                            val district = parts[2].trim()
                            val level = parts[3].trim()

                            viewModel.addScout(
                                name = name,
                                age = age,
                                district = district,
                                level = level
                            )
                        }
                    }

                    batchText = ""
                },

                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),

                shape = RoundedCornerShape(18.dp)
            ) {

                Text("Save Batch")
            }
        }
    }
}
@Composable
fun AuthScreen(
    name: String,
    age: String,
    gender: String,
    district: String,
    mobile: String,
    email: String,
    imageUri: String?,
    onImageChange: (String?) -> Unit,
    onSave: (
        name: String,
        age: String,
        gender: String,
        district: String,
        mobile: String,
        email: String
    ) -> Unit,
    onLogout: () -> Unit
) {
    var isEditing by remember { mutableStateOf(false) }

    var editName by remember { mutableStateOf(name) }
    var editAge by remember { mutableStateOf(age) }
    var editGender by remember { mutableStateOf(gender) }
    var editDistrict by remember { mutableStateOf(district) }
    var editMobile by remember { mutableStateOf(mobile) }
    var editEmail by remember { mutableStateOf(email) }

    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        onImageChange(uri?.toString())
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF070D1D))
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                "Teacher Profile",
                color = Color.White,
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold
            )
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF1B2638)
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(22.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(110.dp)
                            .background(Color(0xFF2563EB), CircleShape)
                            .clickable {
                                imagePicker.launch("image/*")
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        if (imageUri != null) {
                            Image(
                                painter = rememberAsyncImagePainter(imageUri),
                                contentDescription = "Profile Picture",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Text("👤", fontSize = 50.sp)
                        }
                    }

                    Text(
                        "Tap photo to change",
                        color = Color.Gray,
                        fontSize = 13.sp
                    )

                    if (isEditing) {
                        LoginField(editName, { editName = it }, "Name")
                        LoginField(editAge, { editAge = it }, "Age")

                        DropdownField(
                            label = "Gender",
                            selectedValue = editGender,
                            options = listOf("Male", "Female", "Other"),
                            onValueSelected = { editGender = it }
                        )

                        LoginField(editDistrict, { editDistrict = it }, "District")
                        LoginField(editMobile, { editMobile = it }, "Mobile Number")
                        LoginField(editEmail, { editEmail = it }, "Email ID")

                        Button(
                            onClick = {
                                onSave(
                                    editName,
                                    editAge,
                                    editGender,
                                    editDistrict,
                                    editMobile,
                                    editEmail
                                )
                                isEditing = false
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Text("Save Profile")
                        }
                    } else {
                        Text(
                            name,
                            color = Color.White,
                            fontSize = 26.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Text(
                            "School Scout Admin",
                            color = Color(0xFFFFA31A),
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )

                        ProfileInfo("Age", age)
                        ProfileInfo("Gender", gender)
                        ProfileInfo("District", district)
                        ProfileInfo("Mobile", mobile)
                        ProfileInfo("Email", email)

                        Button(
                            onClick = { isEditing = true },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF2563EB)
                            )
                        ) {
                            Text("Edit Profile")
                        }

                        Button(
                            onClick = onLogout,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFFF5252)
                            )
                        ) {
                            Text("Logout")
                        }
                    }
                }
            }
        }
    }
}
@Composable
fun LoginScreen(
    viewModel: ScoutViewModel = viewModel(),
    onLoginSuccess: (
        name: String,
        age: String,
        gender: String,
        district: String,
        mobile: String,
        email: String
    ) -> Unit
) {
    var isNewUser by remember { mutableStateOf(false) }

    var name by remember { mutableStateOf("") }
    var age by remember { mutableStateOf("") }
    var gender by remember { mutableStateOf("") }
    var district by remember { mutableStateOf("") }
    var mobile by remember { mutableStateOf("") }
    var emailOrMobile by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var state by remember { mutableStateOf("") }
    var loginError by remember { mutableStateOf("") }

    val scope = rememberCoroutineScope()
    val genderOptions = listOf("Male", "Female", "Other")

    val stateDistricts = mapOf(
        "Karnataka" to listOf("Bagalkot", "Bengaluru Urban", "Bengaluru Rural", "Belagavi", "Bidar", "Chikkamagaluru", "Dharwad", "Kalaburagi", "Mysuru", "Raichur", "Shivamogga", "Udupi"),
        "Maharashtra" to listOf("Mumbai", "Pune", "Nagpur", "Nashik", "Thane", "Kolhapur", "Solapur"),
        "Tamil Nadu" to listOf("Chennai", "Coimbatore", "Madurai", "Salem", "Tiruchirappalli"),
        "Kerala" to listOf("Thiruvananthapuram", "Kochi", "Kozhikode", "Thrissur"),
        "Telangana" to listOf("Hyderabad", "Warangal", "Nizamabad", "Karimnagar"),
        "Andhra Pradesh" to listOf("Visakhapatnam", "Vijayawada", "Guntur", "Tirupati"),
        "Delhi" to listOf("New Delhi", "Central Delhi", "South Delhi", "North Delhi"),
        "Gujarat" to listOf("Ahmedabad", "Surat", "Vadodara", "Rajkot"),
        "Rajasthan" to listOf("Jaipur", "Jodhpur", "Udaipur", "Kota"),
        "Uttar Pradesh" to listOf("Lucknow", "Kanpur", "Varanasi", "Agra", "Prayagraj"),
        "West Bengal" to listOf("Kolkata", "Howrah", "Darjeeling", "Siliguri"),
        "Madhya Pradesh" to listOf("Bhopal", "Indore", "Gwalior", "Jabalpur")
    )

    val stateOptions = stateDistricts.keys.toList()
    val districtOptions = stateDistricts[state] ?: emptyList()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF070D1D))
            .padding(24.dp),
        contentPadding = PaddingValues(bottom = 40.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            Card(
                shape = RoundedCornerShape(30.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF111827)
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(26.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("🏃", fontSize = 52.sp)

                    Text("Welcome to", color = Color.Gray, fontSize = 16.sp)

                    Text(
                        "Kreeda Prerana Scout",
                        color = Color.White,
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        if (isNewUser) "Create Teacher Profile" else "Teacher Login",
                        color = Color(0xFF3B82F6),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )

                    if (isNewUser) {
                        LoginField(name, { name = it }, "Full Name")
                        LoginField(age, { age = it }, "Age")

                        DropdownField(
                            label = "Gender",
                            selectedValue = gender,
                            options = genderOptions,
                            onValueSelected = { gender = it }
                        )

                        DropdownField(
                            label = "State",
                            selectedValue = state,
                            options = stateOptions,
                            onValueSelected = {
                                state = it
                                district = ""
                            }
                        )

                        DropdownField(
                            label = "District",
                            selectedValue = district,
                            options = districtOptions,
                            onValueSelected = { district = it }
                        )

                        LoginField(mobile, { mobile = it }, "Mobile Number")
                        LoginField(email, { email = it }, "Email ID")
                        LoginField(password, { password = it }, "Set Password")
                        LoginField(confirmPassword, { confirmPassword = it }, "Confirm Password")
                    } else {
                        LoginField(emailOrMobile, { emailOrMobile = it }, "Email or Mobile")
                        LoginField(password, { password = it }, "Password")
                        //loginError = "Please fill all fields and confirm password correctly"
                    }

                    Button(
                        onClick = {

                            if (isNewUser) {

                                if (
                                    name.isNotEmpty() &&
                                    age.isNotEmpty() &&
                                    gender.isNotEmpty() &&
                                    district.isNotEmpty() &&
                                    mobile.isNotEmpty() &&
                                    email.isNotEmpty() &&
                                    password.isNotEmpty() &&
                                    confirmPassword.isNotEmpty() &&
                                    password == confirmPassword
                                ) {

                                    scope.launch {

                                        viewModel.registerTeacher(
                                            Teacher(
                                                name = name,
                                                age = age,
                                                gender = gender,
                                                district = district,
                                                mobile = mobile,
                                                email = email,
                                                password = password
                                            )
                                        )

                                        onLoginSuccess(
                                            name,
                                            age,
                                            gender,
                                            district,
                                            mobile,
                                            email
                                        )
                                    }
                                }

                            } else {

                                scope.launch {

                                    val teacher = viewModel.loginTeacher(
                                        emailOrMobile,
                                        password
                                    )

                                    if (teacher != null) {

                                        onLoginSuccess(
                                            teacher.name,
                                            teacher.age,
                                            teacher.gender,
                                            teacher.district,
                                            teacher.mobile,
                                            teacher.email
                                        )

                                    } else {

                                        loginError =
                                            "Invalid Email/Mobile or Password"
                                    }
                                }
                            }
                        },

                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(18.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF2563EB)
                        )
                    ) {
                        Text(
                            if (isNewUser) "Create Profile & Continue" else "Login",
                            fontSize = 16.sp
                        )
                    }
                    if (loginError.isNotEmpty()) {

                        Text(
                            loginError,
                            color = Color.Red,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Text(
                        if (isNewUser)
                            "Already have account? Login"
                        else
                            "New teacher? Create profile",
                        color = Color(0xFFFFA31A),
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.clickable {
                            isNewUser = !isNewUser
                        }
                    )
                }
            }
        }
    }
}
@Composable
fun LoginField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White,
            focusedContainerColor = Color(0xFF1B2638),
            unfocusedContainerColor = Color(0xFF1B2638),
            focusedBorderColor = Color(0xFF3B82F6),
            unfocusedBorderColor = Color.Gray,
            focusedLabelColor = Color(0xFF3B82F6),
            unfocusedLabelColor = Color.Gray,
            cursorColor = Color.White
        )
    )
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DropdownField(
    label: String,
    selectedValue: String,
    options: List<String>,
    onValueSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = {
            expanded = !expanded
        }
    ) {
        OutlinedTextField(
            value = selectedValue,
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(),
            shape = RoundedCornerShape(18.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                focusedContainerColor = Color(0xFF1B2638),
                unfocusedContainerColor = Color(0xFF1B2638),
                focusedBorderColor = Color(0xFF3B82F6),
                unfocusedBorderColor = Color.Gray,
                focusedLabelColor = Color(0xFF3B82F6),
                unfocusedLabelColor = Color.Gray
            )
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = {
                expanded = false
            }
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = {
                        Text(option)
                    },
                    onClick = {
                        onValueSelected(option)
                        expanded = false
                    }
                )
            }
        }
    }
}
@Composable
fun ProfileInfo(
    label: String,
    value: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, color = Color.Gray)
        Text(
            if (value.isNotEmpty()) value else "-",
            color = Color.White,
            fontWeight = FontWeight.Bold
        )
    }
}