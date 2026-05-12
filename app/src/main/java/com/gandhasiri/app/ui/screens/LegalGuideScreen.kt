package com.gandhasiri.app.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import androidx.navigation.NavController
import com.gandhasiri.app.ui.theme.*
import com.gandhasiri.app.utils.LegalGuide

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LegalGuideScreen(navController: NavController) {
    val steps = remember { LegalGuide.getHarvestingSteps() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Legal Harvesting Guide", color = SandalwoodIvory) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, null, tint = SandalwoodIvory)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = SandalwoodBrown)
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding).background(MaterialTheme.colorScheme.background),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(0.dp)
        ) {
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF3E0)),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, GoldenSap),
                    modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
                ) {
                    Row(modifier = Modifier.padding(16.dp)) {
                        Text("⚖️", fontSize = 28.sp)
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text("Karnataka Sandalwood Law", fontWeight = FontWeight.Bold, color = SandalwoodDark)
                            Text(
                                "Under the Karnataka Forest Act, private landowners CAN grow and sell sandalwood. Follow these steps for legal harvesting. Selling without permits is a criminal offense.",
                                color = SandalwoodBrown, fontSize = 13.sp
                            )
                        }
                    }
                }
            }

            item {
                Text("5-Step Legal Harvesting Process", fontWeight = FontWeight.Bold, color = SandalwoodDark, fontSize = 18.sp, modifier = Modifier.padding(bottom = 16.dp))
            }

            itemsIndexed(steps) { index, step ->
                LegalStepCard(step = step, isLast = index == steps.size - 1)
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9)),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, ForestGreenLight)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("📞 Key Contacts", fontWeight = FontWeight.Bold, color = ForestGreen)
                        Spacer(modifier = Modifier.height(8.dp))
                        ContactInfoRow("District Forest Officer (DFO)", "Visit your district forest office")
                        ContactInfoRow("KFPCL Helpline", "1800-XXX-XXXX (Toll Free)")
                        ContactInfoRow("Forest Department Helpline", "1926")
                        ContactInfoRow("Anti-Smuggling Hotline", "Contact local police: 100")
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFE3F2FD)),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, Color(0xFF1976D2))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("💡 Pro Tip: Use Gandha-Siri as Evidence", fontWeight = FontWeight.Bold, color = Color(0xFF1565C0))
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "Your Gandha-Siri tree register with GPS coordinates, photos, girth measurements, and timestamps serves as strong digital evidence of ownership and legal planting. Export this data when applying to the Forest Department.",
                            color = Color(0xFF1565C0).copy(alpha = 0.8f), fontSize = 13.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun LegalStepCard(step: LegalGuide.LegalStep, isLast: Boolean) {
    var expanded by remember { mutableStateOf(false) }

    Row(modifier = Modifier.fillMaxWidth()) {
        // Timeline indicator
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier = Modifier.size(40.dp).background(SandalwoodBrown, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(step.stepNumber.toString(), color = SandalwoodIvory, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
            if (!isLast) {
                Box(modifier = Modifier.width(2.dp).height(16.dp).background(SandalwoodPale))
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        Card(
            modifier = Modifier.weight(1f).clickable { expanded = !expanded }.padding(bottom = if (isLast) 0.dp else 8.dp),
            colors = CardDefaults.cardColors(containerColor = SandalwoodCream),
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(1.dp, SandalwoodPale)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(step.title, fontWeight = FontWeight.SemiBold, color = SandalwoodDark)
                        Text(step.authority, color = SandalwoodMedium, fontSize = 11.sp)
                    }
                    Icon(
                        if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        null, tint = SandalwoodBrown
                    )
                }

                if (expanded) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(step.description, color = SandalwoodBrown, fontSize = 13.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Required Documents:", color = SandalwoodDark, fontWeight = FontWeight.Medium, fontSize = 12.sp)
                    step.documents.forEach { doc ->
                        Row(modifier = Modifier.padding(top = 4.dp)) {
                            Text("•", color = SandalwoodBrown, modifier = Modifier.padding(end = 6.dp))
                            Text(doc, color = SandalwoodBrown, fontSize = 12.sp)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ContactInfoRow(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
        Text("• $label:", color = ForestGreen, fontWeight = FontWeight.Medium, fontSize = 13.sp, modifier = Modifier.weight(0.5f))
        Text(value, color = Color(0xFF2E7D32), fontSize = 13.sp, modifier = Modifier.weight(0.5f))
    }
}
