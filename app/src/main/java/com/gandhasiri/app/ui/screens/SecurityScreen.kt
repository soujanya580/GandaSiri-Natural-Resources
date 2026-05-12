package com.gandhasiri.app.ui.screens

import android.Manifest
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.*
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.*
import androidx.navigation.NavController
import com.gandhasiri.app.data.entities.SecurityAlert
import com.gandhasiri.app.ui.components.SandalTextField
import com.gandhasiri.app.ui.theme.*
import com.gandhasiri.app.utils.LegalGuide
import com.gandhasiri.app.viewmodel.GandhaSiriViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.android.gms.location.LocationServices
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun SecurityScreen(navController: NavController, viewModel: GandhaSiriViewModel) {
    val context = LocalContext.current
    val activeAlerts by viewModel.activeAlerts.collectAsState()
    val allContacts by viewModel.allContacts.collectAsState()
    val uiMessage by viewModel.uiMessage.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    var currentLat by remember { mutableStateOf(0.0) }
    var currentLon by remember { mutableStateOf(0.0) }
    var panicPressed by remember { mutableStateOf(false) }
    var showConfirmDialog by remember { mutableStateOf(false) }
    var alertDescription by remember { mutableStateOf("") }
    val checklist = LegalGuide.getSecurityChecklist()
    var checkedItems by remember { mutableStateOf(setOf<Int>()) }

    val permissionsState = rememberMultiplePermissionsState(
        listOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.SEND_SMS, Manifest.permission.POST_NOTIFICATIONS)
    )

    val pulseAnim = rememberInfiniteTransition(label = "pulse")
    val pulseScale by pulseAnim.animateFloat(
        initialValue = 1f, targetValue = 1.08f,
        animationSpec = infiniteRepeatable(tween(800, easing = EaseInOutSine), RepeatMode.Reverse),
        label = "scale"
    )

    LaunchedEffect(uiMessage) {
        uiMessage?.let { snackbarHostState.showSnackbar(it); viewModel.clearMessage() }
    }

    fun fetchLocationAndAlert(description: String) {
        try {
            val fusedClient = LocationServices.getFusedLocationProviderClient(context)
            fusedClient.lastLocation.addOnSuccessListener { loc ->
                val lat = loc?.latitude ?: 0.0
                val lon = loc?.longitude ?: 0.0
                viewModel.triggerPanicAlert(context, lat, lon, description)
            }
        } catch (e: SecurityException) {
            viewModel.triggerPanicAlert(context, 0.0, 0.0, description)
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Security Center", color = SandalwoodIvory) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, null, tint = SandalwoodIvory)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = AlertRed)
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding).background(MaterialTheme.colorScheme.background),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // PANIC BUTTON
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE), contentColor = AlertRed),
                    shape = RoundedCornerShape(20.dp),
                    border = BorderStroke(2.dp, AlertRed)
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("PANIC BUTTON", fontWeight = FontWeight.ExtraBold, fontSize = 18.sp, letterSpacing = 2.sp)
                        Text(
                            "Press to instantly alert all emergency contacts with your GPS location",
                            color = AlertRed.copy(alpha = 0.7f), fontSize = 12.sp,
                            textAlign = TextAlign.Center, modifier = Modifier.padding(vertical = 8.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        Box(
                            modifier = Modifier
                                .size(130.dp)
                                .scale(pulseScale)
                                .background(
                                    Brush.radialGradient(listOf(AlertRed, Color(0xFFB71C1C))),
                                    CircleShape
                                )
                                .clickable { showConfirmDialog = true },
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("🚨", fontSize = 36.sp)
                                Text("SOS", color = Color.White, fontWeight = FontWeight.ExtraBold, fontSize = 20.sp, letterSpacing = 4.sp)
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            "Will notify ${allContacts.size} contact${if (allContacts.size != 1) "s" else ""}",
                            color = SandalwoodMedium, fontSize = 12.sp
                        )
                        if (allContacts.isEmpty()) {
                            TextButton(onClick = { navController.navigate(Screen.Contacts.route) }) {
                                Text("Add contacts first →", color = AlertRed)
                            }
                        }
                    }
                }
            }

            // Active alerts
            if (activeAlerts.isNotEmpty()) {
                item {
                    Text("🔴 Active Alerts (${activeAlerts.size})", fontWeight = FontWeight.SemiBold, color = AlertRed)
                }
                items(activeAlerts, key = { it.id }) { alert ->
                    AlertCard(alert) { viewModel.resolveAlert(alert) }
                }
            }

            // Security checklist
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = SandalwoodCream, contentColor = SandalwoodDark),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, SandalwoodPale)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("🔒 Security Checklist", fontWeight = FontWeight.SemiBold)
                        Text(
                            "${checkedItems.size}/${checklist.size} completed",
                            color = if (checkedItems.size == checklist.size) ForestGreen else SandalwoodMedium,
                            fontSize = 12.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        checklist.forEachIndexed { i, (title, desc) ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        checkedItems = if (i in checkedItems) checkedItems - i else checkedItems + i
                                    }
                                    .padding(vertical = 6.dp),
                                verticalAlignment = Alignment.Top
                            ) {
                                Checkbox(
                                    checked = i in checkedItems,
                                    onCheckedChange = { checked ->
                                        checkedItems = if (checked) checkedItems + i else checkedItems - i
                                    },
                                    colors = CheckboxDefaults.colors(checkedColor = ForestGreen)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(title, fontWeight = FontWeight.Medium, fontSize = 14.sp)
                                    Text(desc, color = SandalwoodMedium, fontSize = 11.sp)
                                }
                            }
                            if (i < checklist.size - 1) Divider(color = SandalwoodPale.copy(alpha = 0.4f))
                        }
                    }
                }
            }
        }
    }

    if (showConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showConfirmDialog = false },
            title = { Text("🚨 Confirm Security Alert", color = AlertRed, fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Describe the suspicious activity:", color = SandalwoodDark)
                    SandalTextField(
                        value = alertDescription,
                        onValueChange = { alertDescription = it },
                        label = "e.g. Unknown people near trees at night",
                        singleLine = false
                    )
                    Text(
                        "This will send an SMS and notification to all ${allContacts.size} emergency contacts.",
                        color = SandalwoodMedium, fontSize = 12.sp
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showConfirmDialog = false
                        if (permissionsState.allPermissionsGranted) {
                            fetchLocationAndAlert(alertDescription.ifBlank { "Suspicious activity near sandalwood farm" })
                        } else {
                            permissionsState.launchMultiplePermissionRequest()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AlertRed)
                ) { Text("🚨 SEND ALERT NOW", color = Color.White) }
            },
            dismissButton = {
                TextButton(onClick = { showConfirmDialog = false }) { Text("Cancel", color = SandalwoodDark) }
            }
        )
    }
}

@Composable
fun AlertCard(alert: SecurityAlert, onResolve: () -> Unit) {
    val dateFormat = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault())
    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE), contentColor = AlertRed),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, AlertRed.copy(alpha = 0.5f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("🚨", fontSize = 20.sp)
                Spacer(modifier = Modifier.width(8.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(dateFormat.format(alert.alertTime), fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                    Text(alert.description, color = SandalwoodDark, fontSize = 13.sp)
                }
            }
            if (alert.notifiedContacts.isNotEmpty()) {
                Text("Notified: ${alert.notifiedContacts.joinToString(", ")}", color = SandalwoodMedium, fontSize = 11.sp)
            }
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedButton(
                onClick = onResolve,
                colors = ButtonDefaults.outlinedButtonColors(contentColor = ForestGreen),
                border = BorderStroke(1.dp, ForestGreen), modifier = Modifier.fillMaxWidth()
            ) { Text("✅ Mark Resolved") }
        }
    }
}
