package com.gandhasiri.app.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.*
import androidx.navigation.NavController
import com.gandhasiri.app.data.entities.SandalwoodTree
import com.gandhasiri.app.ui.components.SandalTextField
import com.gandhasiri.app.ui.theme.*
import com.gandhasiri.app.utils.HeartwoodEstimate
import com.gandhasiri.app.utils.TreeCalculator
import com.gandhasiri.app.viewmodel.GandhaSiriViewModel
import java.text.NumberFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MaturityCalculatorScreen(navController: NavController, viewModel: GandhaSiriViewModel) {
    val allTrees by viewModel.allTrees.collectAsState()
    var girthInput by remember { mutableStateOf("") }
    var ageInput by remember { mutableStateOf("") }
    var estimate by remember { mutableStateOf<HeartwoodEstimate?>(null) }
    val currencyFormat = NumberFormat.getCurrencyInstance(Locale("en", "IN"))

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Maturity Calculator", color = SandalwoodIvory) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, null, tint = SandalwoodIvory)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = GoldenSap)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState())
                .background(MaterialTheme.colorScheme.background).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Card(
                colors = CardDefaults.cardColors(containerColor = SandalwoodCream, contentColor = SandalwoodDark),
                shape = RoundedCornerShape(20.dp),
                border = BorderStroke(1.dp, GoldenSap)
            ) {
                Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("🌿", fontSize = 28.sp)
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text("Heartwood Estimator", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                            Text("Enter tree details to estimate harvest readiness", color = SandalwoodMedium, fontSize = 12.sp)
                        }
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Box(modifier = Modifier.weight(1f)) {
                            SandalTextField(
                                value = girthInput,
                                onValueChange = { girthInput = it },
                                label = "Girth (cm)",
                                keyboardType = KeyboardType.Decimal
                            )
                        }
                        Box(modifier = Modifier.weight(1f)) {
                            SandalTextField(
                                value = ageInput,
                                onValueChange = { ageInput = it },
                                label = "Age (years)",
                                keyboardType = KeyboardType.Number
                            )
                        }
                    }

                    Button(
                        onClick = {
                            val girth = girthInput.toDoubleOrNull() ?: return@Button
                            val age = ageInput.toIntOrNull() ?: return@Button
                            estimate = TreeCalculator.calculateHeartwoodEstimate(girth, age)
                        },
                        modifier = Modifier.fillMaxWidth().height(50.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = GoldenSap),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.Calculate, null, tint = SandalwoodDark)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Calculate Estimate", fontWeight = FontWeight.SemiBold, color = SandalwoodDark)
                    }
                }
            }

            estimate?.let { est ->
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = if (est.canHarvest) Color(0xFFE8F5E9) else Color(0xFFFFF8E1),
                        contentColor = SandalwoodDark
                    ),
                    shape = RoundedCornerShape(20.dp),
                    border = BorderStroke(2.dp, if (est.canHarvest) ForestGreenLight else GoldenSap)
                ) {
                    Column(modifier = Modifier.padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(if (est.canHarvest) "🌟 READY TO HARVEST!" else "⏳ Still Growing...", fontSize = 20.sp, fontWeight = FontWeight.ExtraBold,
                            color = if (est.canHarvest) ForestGreen else SandalwoodBrown, textAlign = TextAlign.Center)

                        Spacer(modifier = Modifier.height(16.dp))

                        Box(modifier = Modifier.size(120.dp), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(
                                progress = (est.maturityPercent / 100f).coerceIn(0f, 1f),
                                modifier = Modifier.fillMaxSize(),
                                strokeWidth = 10.dp,
                                color = if (est.canHarvest) ForestGreenLight else GoldenSap,
                                trackColor = SandalwoodPale
                            )
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("${est.maturityPercent}%", fontWeight = FontWeight.ExtraBold, fontSize = 24.sp)
                                Text("mature", fontSize = 11.sp, color = SandalwoodMedium)
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                            ResultMetric("⏰ Years Left", if (est.canHarvest) "Ready!" else "${est.yearsToHarvest} years")
                            ResultMetric("💎 Heartwood", "${"%.1f".format(est.heartwoodDiameterCm)} cm dia")
                            ResultMetric("💰 Est. Value", currencyFormat.format(est.estimatedValueINR))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ResultMetric(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, fontWeight = FontWeight.Bold, color = SandalwoodDark, fontSize = 15.sp)
        Text(label, color = SandalwoodMedium, fontSize = 10.sp, textAlign = TextAlign.Center)
    }
}
