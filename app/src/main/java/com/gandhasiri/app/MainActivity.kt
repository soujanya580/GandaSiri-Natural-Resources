package com.gandhasiri.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.gandhasiri.app.ui.screens.GandhaSiriNavHost
import com.gandhasiri.app.ui.theme.GandhaSiriTheme
import com.gandhasiri.app.utils.NotificationHelper
import com.gandhasiri.app.viewmodel.GandhaSiriViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: GandhaSiriViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Create notification channels on startup
        NotificationHelper.createNotificationChannels(this)

        setContent {
            GandhaSiriTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    GandhaSiriApp(viewModel)
                }
            }
        }
    }
}

@Composable
fun GandhaSiriApp(viewModel: GandhaSiriViewModel) {
    val navController = rememberNavController()
    GandhaSiriNavHost(navController = navController, viewModel = viewModel)
}
