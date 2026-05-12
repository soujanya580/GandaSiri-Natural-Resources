package com.gandhasiri.app.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import androidx.navigation.NavController
import com.gandhasiri.app.data.entities.GrowthRecord
import com.gandhasiri.app.data.entities.HealthStatus
import com.gandhasiri.app.ui.components.SandalTextField
import com.gandhasiri.app.ui.theme.*
import com.gandhasiri.app.viewmodel.GandhaSiriViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GrowthTrackerScreen(treeId: String, navController: NavController, viewModel: GandhaSiriViewModel) {
    val growthRecords by viewModel.getGrowthRecords(treeId).collectAsState(initial = emptyList())
    val tree by viewModel.getTreeById(treeId).collectAsState(initial = null)
    var showAddDialog by remember { mutableStateOf(false) }
    val uiMessage by viewModel.uiMessage.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiMessage) {
        uiMessage?.let { snackbarHostState.showSnackbar(it); viewModel.clearMessage() }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Growth Tracker - ${tree?.nickname ?: treeId}", color = SandalwoodIvory) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, null, tint = SandalwoodIvory)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = ForestGreen)
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = ForestGreen, contentColor = Color.White
            ) {
                Icon(Icons.Default.Add, "Add Growth Record")
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding).background(MaterialTheme.colorScheme.background),
            contentPadding = PaddingValues(16.dp, 16.dp, 16.dp, 80.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Simple growth chart (canvas-based)
            if (growthRecords.size >= 2) {
                item { GrowthChartCard(growthRecords) }
            }

            item {
                Text(
                    "Growth History (${growthRecords.size} records)",
                    fontWeight = FontWeight.SemiBold, color = SandalwoodDark,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }

            if (growthRecords.isEmpty()) {
                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = SandalwoodCream, contentColor = SandalwoodDark),
                        shape = RoundedCornerShape(16.dp), modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(32.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("📊", fontSize = 40.sp)
                            Text("No records yet", fontWeight = FontWeight.SemiBold)
                            Text("Tap + to log your first measurement", color = SandalwoodMedium, fontSize = 13.sp)
                        }
                    }
                }
            } else {
                items(growthRecords.reversed(), key = { it.id }) { record ->
                    GrowthRecordCard(record)
                }
            }
        }
    }

    if (showAddDialog) {
        AddGrowthRecordDialog(
            treeId = treeId,
            currentGirth = tree?.girthCm ?: 0.0,
            currentHeight = tree?.heightM ?: 0.0,
            onDismiss = { showAddDialog = false },
            onConfirm = { girth, height, status, notes ->
                viewModel.addGrowthRecord(treeId, girth, height, status, notes)
                showAddDialog = false
            }
        )
    }
}

@Composable
fun GrowthChartCard(records: List<GrowthRecord>) {
    Card(
        colors = CardDefaults.cardColors(containerColor = SandalwoodCream, contentColor = SandalwoodDark),
        shape = RoundedCornerShape(16.dp), border = BorderStroke(1.dp, SandalwoodPale)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("📈 Girth Growth Chart", fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.height(12.dp))

            val maxGirth = records.maxOf { it.girthCm }
            val minGirth = records.minOf { it.girthCm }.coerceAtMost(maxGirth - 1.0)

            Canvas(modifier = Modifier.fillMaxWidth().height(120.dp)) {
                val width = size.width
                val height = size.height
                val padding = 20f
                val chartWidth = width - 2 * padding
                val chartHeight = height - 2 * padding

                val path = Path()
                val fillPath = Path()

                records.forEachIndexed { index, record ->
                    val x = padding + (index.toFloat() / (records.size - 1)) * chartWidth
                    val normalized = ((record.girthCm - minGirth) / (maxGirth - minGirth)).toFloat()
                    val y = padding + (1 - normalized) * chartHeight

                    if (index == 0) {
                        path.moveTo(x, y)
                        fillPath.moveTo(x, height)
                        fillPath.lineTo(x, y)
                    } else {
                        path.lineTo(x, y)
                        fillPath.lineTo(x, y)
                    }

                    if (index == records.size - 1) {
                        fillPath.lineTo(x, height)
                    }
                }

                // Draw fill
                drawPath(fillPath, color = GoldenSap.copy(alpha = 0.2f))
                // Draw line
                drawPath(path, color = SandalwoodBrown, style = androidx.compose.ui.graphics.drawscope.Stroke(width = 3f))

                // Draw points
                records.forEachIndexed { index, record ->
                    val x = padding + (index.toFloat() / (records.size - 1)) * chartWidth
                    val normalized = ((record.girthCm - minGirth) / (maxGirth - minGirth)).toFloat()
                    val y = padding + (1 - normalized) * chartHeight
                    drawCircle(color = SandalwoodBrown, radius = 6f, center = androidx.compose.ui.geometry.Offset(x, y))
                    drawCircle(color = GoldenSap, radius = 3f, center = androidx.compose.ui.geometry.Offset(x, y))
                }
            }

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Min: ${records.minOf { it.girthCm }}cm", fontSize = 11.sp, color = SandalwoodMedium)
                Text("Growth: +${"%.1f".format(records.last().girthCm - records.first().girthCm)}cm", fontSize = 11.sp, color = ForestGreen, fontWeight = FontWeight.SemiBold)
                Text("Max: ${records.maxOf { it.girthCm }}cm", fontSize = 11.sp, color = SandalwoodMedium)
            }
        }
    }
}

@Composable
fun GrowthRecordCard(record: GrowthRecord) {
    val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
    val healthColor = when (record.healthStatus) {
        HealthStatus.EXCELLENT -> ForestGreenLight
        HealthStatus.GOOD -> Color(0xFF8BC34A)
        HealthStatus.FAIR -> Color(0xFFFFEB3B)
        HealthStatus.POOR -> Color(0xFFFF9800)
        HealthStatus.CRITICAL -> AlertRed
    }
    val healthEmoji = when (record.healthStatus) {
        HealthStatus.EXCELLENT -> "🌟"
        HealthStatus.GOOD -> "✅"
        HealthStatus.FAIR -> "⚠️"
        HealthStatus.POOR -> "🔶"
        HealthStatus.CRITICAL -> "🚨"
    }

    Card(
        colors = CardDefaults.cardColors(containerColor = SandalwoodCream, contentColor = SandalwoodDark),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, SandalwoodPale)
    ) {
        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier.size(44.dp).background(healthColor.copy(alpha = 0.2f), RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center
            ) { Text(healthEmoji, fontSize = 22.sp) }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(dateFormat.format(record.recordedDate), fontWeight = FontWeight.SemiBold)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("⭕ ${record.girthCm}cm", fontSize = 12.sp, color = SandalwoodBrown)
                    Text("↕ ${record.heightM}m", fontSize = 12.sp, color = SandalwoodBrown)
                }
                if (record.notes.isNotBlank()) {
                    Text(record.notes, fontSize = 11.sp, color = SandalwoodMedium)
                }
            }

            Text(
                record.healthStatus.name,
                color = healthColor,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.background(healthColor.copy(alpha = 0.15f), RoundedCornerShape(6.dp)).padding(4.dp)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddGrowthRecordDialog(
    treeId: String,
    currentGirth: Double,
    currentHeight: Double,
    onDismiss: () -> Unit,
    onConfirm: (Double, Double, HealthStatus, String) -> Unit
) {
    var girth by remember { mutableStateOf(currentGirth.toString()) }
    var height by remember { mutableStateOf(currentHeight.toString()) }
    var selectedHealth by remember { mutableStateOf(HealthStatus.GOOD) }
    var notes by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Log Growth Measurement", color = SandalwoodDark) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    SandalTextField(value = girth, onValueChange = { girth = it }, label = "Girth (cm)",
                        keyboardType = androidx.compose.ui.text.input.KeyboardType.Decimal, modifier = Modifier.weight(1f))
                    SandalTextField(value = height, onValueChange = { height = it }, label = "Height (m)",
                        keyboardType = androidx.compose.ui.text.input.KeyboardType.Decimal, modifier = Modifier.weight(1f))
                }
                Text("Health Status", color = SandalwoodDark, fontWeight = FontWeight.Medium)
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    HealthStatus.entries.take(3).forEach { status ->
                        FilterChip(
                            selected = selectedHealth == status,
                            onClick = { selectedHealth = status },
                            label = { Text(status.name.take(4), fontSize = 10.sp, color = if (selectedHealth == status) Color.White else SandalwoodDark) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = ForestGreen,
                                containerColor = SandalwoodPale.copy(alpha = 0.3f)
                            )
                        )
                    }
                }
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    HealthStatus.entries.drop(3).forEach { status ->
                        FilterChip(
                            selected = selectedHealth == status,
                            onClick = { selectedHealth = status },
                            label = { Text(status.name.take(5), fontSize = 10.sp, color = if (selectedHealth == status) Color.White else SandalwoodDark) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = ForestGreen,
                                containerColor = SandalwoodPale.copy(alpha = 0.3f)
                            )
                        )
                    }
                }
                SandalTextField(value = notes, onValueChange = { notes = it }, label = "Notes (optional)", singleLine = false)
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(girth.toDoubleOrNull() ?: currentGirth, height.toDoubleOrNull() ?: currentHeight, selectedHealth, notes) },
                colors = ButtonDefaults.buttonColors(containerColor = ForestGreen)
            ) { Text("Save Record", color = Color.White) }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel", color = SandalwoodDark) }
        }
    )
}
