package com.gandhasiri.app.ui.screens

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import androidx.navigation.NavController
import com.gandhasiri.app.data.entities.EmergencyContact
import com.gandhasiri.app.ui.components.SandalTextField
import com.gandhasiri.app.ui.theme.*
import com.gandhasiri.app.viewmodel.GandhaSiriViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContactsScreen(navController: NavController, viewModel: GandhaSiriViewModel) {
    val contacts by viewModel.allContacts.collectAsState()
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
                title = { Text("Emergency Contacts", color = SandalwoodIvory) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, null, tint = SandalwoodIvory)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = SandalwoodBrown)
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = SandalwoodBrown, contentColor = SandalwoodIvory
            ) { Icon(Icons.Default.PersonAdd, "Add Contact") }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding).background(MaterialTheme.colorScheme.background),
            contentPadding = PaddingValues(16.dp, 16.dp, 16.dp, 80.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE)),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, AlertRed.copy(alpha = 0.3f))
                ) {
                    Row(modifier = Modifier.padding(12.dp)) {
                        Text("📞", fontSize = 20.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            "These contacts will be alerted via SMS and notification when you press the Panic Button. Add trusted neighbors, family, or local police.",
                            color = AlertRed.copy(alpha = 0.8f), fontSize = 12.sp
                        )
                    }
                }
            }

            if (contacts.isEmpty()) {
                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = SandalwoodCream, contentColor = SandalwoodDark),
                        shape = RoundedCornerShape(16.dp), modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(32.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("👥", fontSize = 40.sp)
                            Text("No contacts added", fontWeight = FontWeight.SemiBold)
                            Text("Add trusted people who can help in emergencies", color = SandalwoodMedium, fontSize = 13.sp,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center)
                        }
                    }
                }
            } else {
                items(contacts, key = { it.id }) { contact ->
                    ContactCard(contact) { viewModel.deleteContact(contact) }
                }
            }
        }
    }

    if (showAddDialog) {
        AddContactDialog(
            onDismiss = { showAddDialog = false },
            onConfirm = { name, phone, rel -> viewModel.addContact(name, phone, rel); showAddDialog = false }
        )
    }
}

@Composable
fun ContactCard(contact: EmergencyContact, onDelete: () -> Unit) {
    val initials = contact.name.split(" ").take(2).joinToString("") { it.first().toString() }.uppercase()
    val avatarColors = listOf(SandalwoodBrown, ForestGreen, SandalwoodMedium, Color(0xFF1976D2))
    val avatarColor = avatarColors[contact.id.toInt() % avatarColors.size]

    Card(
        colors = CardDefaults.cardColors(containerColor = SandalwoodCream, contentColor = SandalwoodDark),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, SandalwoodPale)
    ) {
        Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier.size(48.dp).background(avatarColor, CircleShape),
                contentAlignment = Alignment.Center
            ) { Text(initials, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp) }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(contact.name, fontWeight = FontWeight.SemiBold)
                Text(contact.phone, color = SandalwoodBrown, fontSize = 14.sp)
                if (contact.relationship.isNotBlank()) {
                    Text(contact.relationship, color = SandalwoodMedium, fontSize = 12.sp)
                }
            }

            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, null, tint = AlertRed.copy(alpha = 0.6f))
            }
        }
    }
}

@Composable
fun AddContactDialog(onDismiss: () -> Unit, onConfirm: (String, String, String) -> Unit) {
    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var relationship by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Emergency Contact", color = SandalwoodDark, fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                SandalTextField(value = name, onValueChange = { name = it }, label = "Full Name")
                SandalTextField(
                    value = phone, onValueChange = { phone = it }, label = "Phone Number",
                    keyboardType = androidx.compose.ui.text.input.KeyboardType.Phone
                )
                SandalTextField(value = relationship, onValueChange = { relationship = it }, label = "Relationship (e.g. Neighbor, Brother)")
            }
        },
        confirmButton = {
            Button(
                onClick = { if (name.isNotBlank() && phone.isNotBlank()) onConfirm(name, phone, relationship) },
                colors = ButtonDefaults.buttonColors(containerColor = SandalwoodBrown)
            ) { Text("Add Contact", color = Color.White) }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel", color = SandalwoodDark) }
        }
    )
}
