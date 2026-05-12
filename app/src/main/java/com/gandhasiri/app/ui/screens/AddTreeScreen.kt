package com.gandhasiri.app.ui.screens

import android.Manifest
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.*
import androidx.core.content.FileProvider
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.gandhasiri.app.ui.components.SandalTextField
import com.gandhasiri.app.ui.theme.*
import com.gandhasiri.app.viewmodel.GandhaSiriViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.android.gms.location.LocationServices
import java.io.File
import java.util.*

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun AddTreeScreen(navController: NavController, viewModel: GandhaSiriViewModel) {
    val context = LocalContext.current
    var nickname by remember { mutableStateOf("") }
    var girthCm by remember { mutableStateOf("") }
    var heightM by remember { mutableStateOf("") }
    var locationDesc by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }
    var photoUri by remember { mutableStateOf<Uri?>(null) }
    var latitude by remember { mutableStateOf(0.0) }
    var longitude by remember { mutableStateOf(0.0) }
    var locationFetched by remember { mutableStateOf(false) }
    var plantedYear by remember { mutableStateOf("") }

    val cameraFileUri = remember { mutableStateOf<Uri?>(null) }

    val permissionsState = rememberMultiplePermissionsState(
        listOf(Manifest.permission.CAMERA, Manifest.permission.ACCESS_FINE_LOCATION)
    )

    val cameraLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) photoUri = cameraFileUri.value
    }

    val galleryLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri -> photoUri = uri }

    fun fetchLocation() {
        try {
            val fusedClient = LocationServices.getFusedLocationProviderClient(context)
            fusedClient.lastLocation.addOnSuccessListener { loc ->
                loc?.let {
                    latitude = it.latitude
                    longitude = it.longitude
                    locationFetched = true
                }
            }
        } catch (e: SecurityException) { /* permission denied */ }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Register New Tree", color = SandalwoodIvory) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, null, tint = SandalwoodIvory)
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
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Photo section
            Card(
                colors = CardDefaults.cardColors(containerColor = SandalwoodCream, contentColor = SandalwoodDark),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, SandalwoodPale)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("📸 Tree Photo", fontWeight = FontWeight.SemiBold)
                    Spacer(modifier = Modifier.height(8.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(160.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(SandalwoodPale),
                        contentAlignment = Alignment.Center
                    ) {
                        if (photoUri != null) {
                            AsyncImage(
                                model = photoUri,
                                contentDescription = null,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = androidx.compose.ui.layout.ContentScale.Crop
                            )
                        } else {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("🌳", fontSize = 48.sp)
                                Text("No photo yet", color = SandalwoodBrown, fontSize = 12.sp)
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedButton(
                            onClick = {
                                if (permissionsState.allPermissionsGranted) {
                                    val imageFile = File.createTempFile("tree_", ".jpg", context.cacheDir)
                                    val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", imageFile)
                                    cameraFileUri.value = uri
                                    cameraLauncher.launch(uri)
                                } else permissionsState.launchMultiplePermissionRequest()
                            },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = SandalwoodBrown),
                            border = BorderStroke(1.dp, SandalwoodBrown)
                        ) {
                            Icon(Icons.Default.CameraAlt, null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Camera")
                        }
                        OutlinedButton(
                            onClick = { galleryLauncher.launch("image/*") },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = SandalwoodBrown),
                            border = BorderStroke(1.dp, SandalwoodBrown)
                        ) {
                            Icon(Icons.Default.Photo, null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Gallery")
                        }
                    }
                }
            }

            // Tree details
            Card(
                colors = CardDefaults.cardColors(containerColor = SandalwoodCream, contentColor = SandalwoodDark),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, SandalwoodPale)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("🌱 Tree Details", fontWeight = FontWeight.SemiBold)

                    SandalTextField(value = nickname, onValueChange = { nickname = it }, label = "Tree Nickname (e.g. North Field #1)")

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        SandalTextField(
                            value = girthCm, onValueChange = { girthCm = it }, label = "Girth (cm)",
                            keyboardType = KeyboardType.Decimal, modifier = Modifier.weight(1f)
                        )
                        SandalTextField(
                            value = heightM, onValueChange = { heightM = it }, label = "Height (m)",
                            keyboardType = KeyboardType.Decimal, modifier = Modifier.weight(1f)
                        )
                    }

                    SandalTextField(
                        value = plantedYear, onValueChange = { plantedYear = it },
                        label = "Year Planted (e.g. 2018)", keyboardType = KeyboardType.Number
                    )
                }
            }

            // Location
            Card(
                colors = CardDefaults.cardColors(containerColor = SandalwoodCream, contentColor = SandalwoodDark),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, SandalwoodPale)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("📍 Location", fontWeight = FontWeight.SemiBold)

                    Button(
                        onClick = {
                            if (permissionsState.allPermissionsGranted) fetchLocation()
                            else permissionsState.launchMultiplePermissionRequest()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = ForestGreen),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.MyLocation, null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(if (locationFetched) "✅ Location Captured" else "Capture GPS Location", color = Color.White)
                    }

                    if (locationFetched) {
                        Text(
                            "📍 Lat: ${"%.5f".format(latitude)}, Lon: ${"%.5f".format(longitude)}",
                            style = MaterialTheme.typography.bodySmall,
                            color = ForestGreen
                        )
                    }

                    SandalTextField(value = locationDesc, onValueChange = { locationDesc = it }, label = "Location Description (e.g. North-East corner, plot 3)")
                    SandalTextField(value = notes, onValueChange = { notes = it }, label = "Additional Notes", singleLine = false)
                }
            }

            // Submit button
            Button(
                onClick = {
                    val girth = girthCm.toDoubleOrNull() ?: 0.0
                    val height = heightM.toDoubleOrNull() ?: 0.0
                    val year = plantedYear.toIntOrNull() ?: Calendar.getInstance().get(Calendar.YEAR)
                    val cal = Calendar.getInstance().apply { set(year, 0, 1) }
                    viewModel.addTree(
                        nickname = nickname.ifBlank { "My Sandalwood Tree" },
                        photoUri = photoUri?.toString(),
                        latitude = latitude,
                        longitude = longitude,
                        girthCm = girth,
                        heightM = height,
                        plantedDate = cal.time,
                        locationDescription = locationDesc,
                        notes = notes
                    )
                    navController.popBackStack()
                },
                modifier = Modifier.fillMaxWidth().height(52.dp),
                colors = ButtonDefaults.buttonColors(containerColor = SandalwoodBrown),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.Save, null, tint = Color.White)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Register Tree", fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = Color.White)
            }
        }
    }
}
