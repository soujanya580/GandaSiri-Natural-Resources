package com.gandhasiri.app.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.*
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.gandhasiri.app.data.entities.SandalwoodTree
import com.gandhasiri.app.ui.theme.*
import com.gandhasiri.app.utils.HeartwoodEstimate
import com.gandhasiri.app.viewmodel.GandhaSiriViewModel
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TreeDetailScreen(treeId: String, navController: NavController, viewModel: GandhaSiriViewModel) {
    val treeFlow = remember(treeId) { viewModel.getTreeById(treeId) }
    val tree by treeFlow.collectAsState(initial = null)
    val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())

    tree?.let { t ->
        val ageYears = viewModel.getTreeAgeYears(t.plantedDate)
        val estimate = viewModel.calculateHeartwoodEstimate(t.girthCm, t.plantedDate)

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(t.nickname, color = SandalwoodIvory) },
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(Icons.Default.ArrowBack, null, tint = SandalwoodIvory)
                        }
                    },
                    actions = {
                        IconButton(onClick = { navController.navigate(Screen.GrowthTracker.createRoute(treeId)) }) {
                            Icon(Icons.Default.ShowChart, null, tint = SandalwoodIvory)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = SandalwoodBrown)
                )
            }
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
                    .background(MaterialTheme.colorScheme.background)
            ) {
                // Hero image
                Box(
                    modifier = Modifier.fillMaxWidth().height(200.dp).background(SandalwoodDark),
                    contentAlignment = Alignment.Center
                ) {
                    if (t.photoUri != null) {
                        AsyncImage(
                            model = t.photoUri,
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Text("🌳", fontSize = 80.sp)
                    }

                    // Tree ID badge overlay
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(12.dp)
                            .background(Color.Black.copy(alpha = 0.6f), RoundedCornerShape(8.dp))
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(t.treeId, color = GoldenSap, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                }

                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {

                    // Quick stats
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        DetailStatCard("🌀 Girth", "${t.girthCm} cm", SandalwoodCream, modifier = Modifier.weight(1f))
                        DetailStatCard("📏 Height", "${t.heightM} m", SandalwoodCream, modifier = Modifier.weight(1f))
                        DetailStatCard("🌱 Age", "$ageYears yrs", SandalwoodCream, modifier = Modifier.weight(1f))
                    }

                    // Maturity card
                    MaturityCard(estimate)

                    // Tree info
                    InfoCard("📋 Tree Information") {
                        InfoRow("Tree ID", t.treeId)
                        InfoRow("Nickname", t.nickname)
                        InfoRow("Planted On", dateFormat.format(t.plantedDate))
                        InfoRow("Registered", dateFormat.format(t.registeredDate))
                        InfoRow("Location", t.locationDescription.ifBlank { "Not specified" })
                        if (t.latitude != 0.0) {
                            InfoRow("GPS", "${"%.5f".format(t.latitude)}, ${"%.5f".format(t.longitude)}")
                        }
                        if (t.notes.isNotBlank()) {
                            InfoRow("Notes", t.notes)
                        }
                    }

                    // Action buttons
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(
                            onClick = { navController.navigate(Screen.GrowthTracker.createRoute(treeId)) },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(containerColor = ForestGreen)
                        ) {
                            Icon(Icons.Default.ShowChart, null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Growth Log")
                        }
                        OutlinedButton(
                            onClick = { viewModel.deleteTree(t); navController.popBackStack() },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = AlertRed),
                            border = BorderStroke(1.dp, AlertRed)
                        ) {
                            Icon(Icons.Default.Delete, null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Remove")
                        }
                    }
                }
            }
        }
    } ?: Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator(color = SandalwoodBrown)
    }
}

@Composable
fun DetailStatCard(label: String, value: String, containerColor: Color, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = containerColor),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, SandalwoodPale)
    ) {
        Column(modifier = Modifier.padding(10.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(value, fontWeight = FontWeight.Bold, color = SandalwoodDark, fontSize = 16.sp)
            Text(label, fontSize = 10.sp, color = SandalwoodMedium, textAlign = TextAlign.Center)
        }
    }
}

@Composable
fun MaturityCard(estimate: HeartwoodEstimate) {
    val currencyFormat = NumberFormat.getCurrencyInstance(Locale("en", "IN"))

    Card(
        colors = CardDefaults.cardColors(
            containerColor = if (estimate.canHarvest) Color(0xFFE8F5E9) else SandalwoodCream
        ),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, if (estimate.canHarvest) ForestGreenLight else GoldenSap)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(if (estimate.canHarvest) "🌟" else "⏳", fontSize = 24.sp)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    if (estimate.canHarvest) "Ready for Harvest!" else "Growing Strong",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (estimate.canHarvest) ForestGreen else SandalwoodDark
                )
            }
            Spacer(modifier = Modifier.height(12.dp))

            LinearProgressIndicator(
                progress = estimate.maturityPercent / 100f,
                modifier = Modifier.fillMaxWidth().height(10.dp).clip(RoundedCornerShape(5.dp)),
                color = if (estimate.canHarvest) ForestGreenLight else GoldenSap,
                trackColor = SandalwoodPale
            )

            Spacer(modifier = Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("${estimate.maturityPercent}% Mature", color = SandalwoodBrown, fontWeight = FontWeight.SemiBold)
                if (!estimate.canHarvest) {
                    Text("~${estimate.yearsToHarvest} years left", color = SandalwoodMedium, fontSize = 12.sp)
                }
            }

            if (estimate.estimatedValueINR > 0) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    "💰 Est. Value: ${currencyFormat.format(estimate.estimatedValueINR)}",
                    color = SandalwoodDark,
                    fontWeight = FontWeight.Medium
                )
            }
            Text(
                "Heartwood diameter: ~${"%.1f".format(estimate.heartwoodDiameterCm)} cm",
                fontSize = 12.sp, color = SandalwoodMedium
            )
        }
    }
}

@Composable
fun InfoCard(title: String, content: @Composable ColumnScope.() -> Unit) {
    Card(
        colors = CardDefaults.cardColors(containerColor = SandalwoodCream),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, SandalwoodPale)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(title, fontWeight = FontWeight.SemiBold, color = SandalwoodDark, modifier = Modifier.padding(bottom = 8.dp))
            content()
        }
    }
}

@Composable
fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, color = SandalwoodMedium, fontSize = 14.sp, modifier = Modifier.weight(0.4f))
        Text(value, color = SandalwoodDark, fontSize = 14.sp, fontWeight = FontWeight.Medium, modifier = Modifier.weight(0.6f))
    }
    Divider(color = SandalwoodPale.copy(alpha = 0.5f), thickness = 0.5.dp)
}
