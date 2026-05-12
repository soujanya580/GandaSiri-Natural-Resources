package com.gandhasiri.app.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.*
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.gandhasiri.app.data.entities.SandalwoodTree
import com.gandhasiri.app.ui.theme.*
import com.gandhasiri.app.viewmodel.GandhaSiriViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: GandhaSiriViewModel
) {
    val trees by viewModel.allTrees.collectAsState()
    val activeAlerts by viewModel.activeAlerts.collectAsState()
    val uiMessage by viewModel.uiMessage.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiMessage) {
        uiMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearMessage()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate(Screen.AddTree.route) },
                containerColor = SandalwoodBrown,
                contentColor = SandalwoodIvory
            ) {
                Icon(Icons.Default.Add, "Add Tree")
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.background),
            contentPadding = PaddingValues(bottom = 80.dp)
        ) {
            item { HomeHeader(trees.size, activeAlerts.size) }

            item {
                QuickActionsRow(navController)
            }

            if (activeAlerts.isNotEmpty()) {
                item {
                    AlertBanner(activeAlerts.size) {
                        navController.navigate(Screen.Security.route)
                    }
                }
            }

            item {
                SectionTitle("My Sandalwood Trees", "${trees.size} registered")
            }

            if (trees.isEmpty()) {
                item { EmptyTreesCard(navController) }
            } else {
                items(trees, key = { it.treeId }) { tree ->
                    TreeCard(
                        tree = tree,
                        viewModel = viewModel,
                        onClick = { navController.navigate(Screen.TreeDetail.createRoute(tree.treeId)) }
                    )
                }
            }
        }
    }
}

@Composable
fun HomeHeader(treeCount: Int, alertCount: Int) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Brush.verticalGradient(
                    colors = listOf(SandalwoodDark, SandalwoodBrown)
                )
            )
            .padding(24.dp)
    ) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("🌿", fontSize = 32.sp)
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        "Gandha-Siri",
                        style = MaterialTheme.typography.headlineMedium,
                        color = SandalwoodIvory,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        "ಗಂಧ-ಶ್ರೀ • Sandalwood Guardian",
                        style = MaterialTheme.typography.bodyMedium,
                        color = SandalwoodPale
                    )
                }
                // Version Tag
                Surface(
                    color = Color.White.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(
                        "v1.3",
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                        color = SandalwoodIvory,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatChip("🌳 Trees", treeCount.toString(), SandalwoodPale)
                StatChip("✅ Active", treeCount.toString(), LeafGreen)
                if (alertCount > 0) {
                    StatChip("🚨 Alerts", alertCount.toString(), Color(0xFFFF6B6B))
                } else {
                    StatChip("🔒 Secure", "All safe", Color(0xFF81C784))
                }
            }
        }
    }
}

@Composable
fun StatChip(label: String, value: String, color: Color) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .background(Color.White.copy(alpha = 0.15f), RoundedCornerShape(12.dp))
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Text(value, color = color, fontWeight = FontWeight.Bold, fontSize = 18.sp)
        Text(label, color = SandalwoodCream, fontSize = 11.sp)
    }
}

@Composable
fun QuickActionsRow(navController: NavController) {
    Column(modifier = Modifier.padding(16.dp)) {
        Text(
            "Quick Actions",
            style = MaterialTheme.typography.titleMedium,
            color = SandalwoodBrown,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(bottom = 12.dp)
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            QuickActionCard("🚨\nPanic\nAlert", AlertRed) {
                navController.navigate(Screen.Security.route)
            }
            QuickActionCard("⚖️\nLegal\nGuide", SandalwoodBrown) {
                navController.navigate(Screen.LegalGuide.route)
            }
            QuickActionCard("📊\nMaturity\nCalc", ForestGreen) {
                navController.navigate(Screen.MaturityCalculator.route)
            }
            QuickActionCard("📞\nContacts", SandalwoodMedium) {
                navController.navigate(Screen.Contacts.route)
            }
        }
    }
}

@Composable
fun QuickActionCard(label: String, color: Color, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .width(80.dp)
            .height(80.dp)
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = color),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
            Text(
                label,
                color = Color.White,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                modifier = Modifier.padding(4.dp)
            )
        }
    }
}

@Composable
fun AlertBanner(count: Int, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE), contentColor = AlertRed),
        border = BorderStroke(1.dp, AlertRed),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("🚨", fontSize = 24.sp)
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    "$count Active Security Alert${if (count > 1) "s" else ""}",
                    fontWeight = FontWeight.Bold
                )
                Text("Tap to view details", color = AlertRed.copy(alpha = 0.7f), fontSize = 12.sp)
            }
            Icon(Icons.Default.ChevronRight, null, tint = AlertRed)
        }
    }
}

@Composable
fun SectionTitle(title: String, subtitle: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically) {
        Column(modifier = Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold, color = SandalwoodDark)
            Text(subtitle, style = MaterialTheme.typography.bodySmall, color = SandalwoodMedium)
        }
    }
}

@Composable
fun EmptyTreesCard(navController: NavController) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(containerColor = SandalwoodCream, contentColor = SandalwoodDark),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("🌱", fontSize = 48.sp)
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                "No trees registered yet",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                "Tap + to register your first sandalwood tree and start protecting your investment.",
                style = MaterialTheme.typography.bodyMedium,
                color = SandalwoodBrown,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                modifier = Modifier.padding(top = 8.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = { navController.navigate(Screen.AddTree.route) },
                colors = ButtonDefaults.buttonColors(containerColor = SandalwoodBrown)
            ) {
                Icon(Icons.Default.Add, null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Register First Tree")
            }
        }
    }
}

@Composable
fun TreeCard(tree: SandalwoodTree, viewModel: GandhaSiriViewModel, onClick: () -> Unit) {
    val ageYears = viewModel.getTreeAgeYears(tree.plantedDate)
    val estimate = viewModel.calculateHeartwoodEstimate(tree.girthCm, tree.plantedDate)
    val dateFormat = SimpleDateFormat("MMM yyyy", Locale.getDefault())

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = SandalwoodCream, contentColor = SandalwoodDark),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(3.dp),
        border = BorderStroke(1.dp, SandalwoodPale)
    ) {
        Row(modifier = Modifier.padding(16.dp)) {
            // Tree photo or placeholder
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(SandalwoodPale),
                contentAlignment = Alignment.Center
            ) {
                if (tree.photoUri != null) {
                    AsyncImage(
                        model = tree.photoUri,
                        contentDescription = "Tree photo",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = androidx.compose.ui.layout.ContentScale.Crop
                    )
                } else {
                    Text("🌳", fontSize = 36.sp)
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        tree.nickname,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Text(
                    tree.treeId,
                    style = MaterialTheme.typography.labelSmall,
                    color = SandalwoodMedium,
                    modifier = Modifier
                        .background(SandalwoodPale.copy(alpha = 0.5f), RoundedCornerShape(4.dp))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    InfoBadge("🔵 ${tree.girthCm}cm girth")
                    InfoBadge("🌱 $ageYears yrs old")
                }
                Spacer(modifier = Modifier.height(4.dp))

                // Maturity progress bar
                LinearProgressIndicator(
                    progress = (estimate.maturityPercent / 100f).coerceIn(0f, 1f),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp)),
                    color = if (estimate.canHarvest) ForestGreenLight else GoldenSap,
                    trackColor = SandalwoodPale
                )
                Text(
                    if (estimate.canHarvest) "✅ Ready for harvest!" else "🌿 ${estimate.maturityPercent}% mature • ${estimate.yearsToHarvest}y to harvest",
                    style = MaterialTheme.typography.labelSmall,
                    color = if (estimate.canHarvest) SafeGreen else SandalwoodMedium,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }

            Icon(
                Icons.Default.ChevronRight,
                null,
                tint = SandalwoodMedium,
                modifier = Modifier.align(Alignment.CenterVertically)
            )
        }
    }
}

@Composable
fun InfoBadge(text: String) {
    Text(
        text,
        style = MaterialTheme.typography.labelSmall,
        color = SandalwoodBrown,
        modifier = Modifier
            .background(SandalwoodPale.copy(alpha = 0.6f), RoundedCornerShape(8.dp))
            .padding(horizontal = 6.dp, vertical = 2.dp)
    )
}
