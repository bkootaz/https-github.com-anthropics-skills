package com.autoai.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.autoai.app.features.chat.ChatScreen
import com.autoai.app.features.settings.SettingsScreen
import com.autoai.app.ui.theme.AutoAITheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { App() }
    }
}

@Composable
fun App() {
    AutoAITheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            val navController = rememberNavController()
            NavHost(navController = navController, startDestination = "chat") {
                composable("chat") { ChatScreen(onOpenSettings = { navController.navigate("settings") }) }
                composable("settings") { SettingsScreen(onBack = { navController.popBackStack() }) }
            }
        }
    }
}
