package com.autoai.app

import android.Manifest
import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.autoai.app.features.assistant.AssistantActivity
import com.autoai.app.receivers.DeviceAdminReceiver
import com.autoai.app.services.VoiceActivationService
import com.autoai.app.ui.theme.AutoAITheme

class MainActivity : ComponentActivity() {
    
    companion object {
        private const val TAG = "MainActivity"
        private const val PERMISSION_REQUEST_CODE = 1001
    }
    
    private lateinit var devicePolicyManager: DevicePolicyManager
    private lateinit var adminComponent: ComponentName
    
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val allGranted = permissions.values.all { it }
        if (allGranted) {
            Log.d(TAG, "All permissions granted")
            startVoiceService()
        } else {
            Log.w(TAG, "Some permissions denied")
        }
    }
    
    private val deviceAdminLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            Log.d(TAG, "Device admin enabled")
        } else {
            Log.w(TAG, "Device admin not enabled")
        }
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Initialize device admin
        devicePolicyManager = getSystemService(DEVICE_POLICY_SERVICE) as DevicePolicyManager
        adminComponent = ComponentName(this, DeviceAdminReceiver::class.java)
        
        setContent {
            AutoAITheme {
                MainScreen(
                    onStartAssistant = { startAssistant() },
                    onRequestPermissions = { requestPermissions() },
                    onEnableDeviceAdmin = { enableDeviceAdmin() },
                    onOpenSettings = { openSettings() }
                )
            }
        }
    }
    
    private fun startAssistant() {
        val intent = Intent(this, AssistantActivity::class.java)
        startActivity(intent)
    }
    
    private fun requestPermissions() {
        val permissions = mutableListOf<String>().apply {
            add(Manifest.permission.RECORD_AUDIO)
            add(Manifest.permission.MODIFY_AUDIO_SETTINGS)
            add(Manifest.permission.SYSTEM_ALERT_WINDOW)
            add(Manifest.permission.USE_FULL_SCREEN_INTENT)
            add(Manifest.permission.SHOW_WHEN_LOCKED)
            add(Manifest.permission.TURN_SCREEN_ON)
            add(Manifest.permission.POST_NOTIFICATIONS)
            add(Manifest.permission.FOREGROUND_SERVICE)
            add(Manifest.permission.FOREGROUND_SERVICE_MICROPHONE)
            
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                add(Manifest.permission.BLUETOOTH_CONNECT)
            }
        }
        
        requestPermissionLauncher.launch(permissions.toTypedArray())
    }
    
    private fun enableDeviceAdmin() {
        if (!devicePolicyManager.isAdminActive(adminComponent)) {
            val intent = Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN).apply {
                putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, adminComponent)
                putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, 
                    "This app needs device admin access to override lock screen for voice assistant.")
            }
            deviceAdminLauncher.launch(intent)
        }
    }
    
    private fun openSettings() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.fromParts("package", packageName, null)
        }
        startActivity(intent)
    }
    
    private fun startVoiceService() {
        val intent = Intent(this, VoiceActivationService::class.java)
        startForegroundService(intent)
    }
    
    private fun checkPermissions(): Boolean {
        val requiredPermissions = listOf(
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.SYSTEM_ALERT_WINDOW,
            Manifest.permission.POST_NOTIFICATIONS
        )
        
        return requiredPermissions.all { permission ->
            ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    onStartAssistant: () -> Unit,
    onRequestPermissions: () -> Unit,
    onEnableDeviceAdmin: () -> Unit,
    onOpenSettings: () -> Unit
) {
    val context = LocalContext.current
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Title
        Text(
            text = "AutoAI Assistant",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        
        Text(
            text = "Your personal voice assistant that works even when your phone is locked",
            fontSize = 16.sp,
            modifier = Modifier.padding(bottom = 32.dp)
        )
        
        // Start Assistant Button
        Button(
            onClick = onStartAssistant,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            Text("Start Assistant", fontSize = 18.sp)
        }
        
        // Request Permissions Button
        Button(
            onClick = onRequestPermissions,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            Text("Grant Permissions", fontSize = 18.sp)
        }
        
        // Enable Device Admin Button
        Button(
            onClick = onEnableDeviceAdmin,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            Text("Enable Device Admin", fontSize = 18.sp)
        }
        
        // Open Settings Button
        OutlinedButton(
            onClick = onOpenSettings,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            Text("Open App Settings", fontSize = 18.sp)
        }
        
        Spacer(modifier = Modifier.height(32.dp))
        
        // Instructions
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Setup Instructions:",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                
                Text(
                    text = "1. Grant all required permissions\n" +
                          "2. Enable device admin access\n" +
                          "3. Enable accessibility service\n" +
                          "4. Say 'Hey Assistant' or 'Hello Assistant' to activate",
                    fontSize = 14.sp
                )
            }
        }
    }
}