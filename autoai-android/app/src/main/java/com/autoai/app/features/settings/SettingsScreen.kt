package com.autoai.app.features.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.autoai.app.data.SettingsStore

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(onBack: () -> Unit, vm: SettingsViewModel = viewModel()) {
    val systemInstructions = remember { mutableStateOf("") }
    val allowlist = remember { mutableStateOf("") }
    val blocklist = remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        val s = vm.loadSettings()
        systemInstructions.value = s.systemInstructions
        allowlist.value = s.allowlist.joinToString(",")
        blocklist.value = s.blocklist.joinToString(",")
    }

    Scaffold(topBar = { TopAppBar(title = { Text("Settings") }) }) { padding ->
        Column(Modifier.padding(padding).padding(16.dp)) {
            Text("System instructions (behavior)")
            OutlinedTextField(
                value = systemInstructions.value,
                onValueChange = { systemInstructions.value = it },
                modifier = Modifier,
                placeholder = { Text("Focus on Ford Falcon and Holden Commodore diagnostics; avoid eBay.") }
            )

            Text("Allowlist domains (comma-separated)", modifier = Modifier.padding(top = 16.dp))
            OutlinedTextField(
                value = allowlist.value,
                onValueChange = { allowlist.value = it }
            )

            Text("Blocklist domains (comma-separated)", modifier = Modifier.padding(top = 16.dp))
            OutlinedTextField(
                value = blocklist.value,
                onValueChange = { blocklist.value = it }
            )

            Button(onClick = {
                vm.saveSettings(
                    systemInstructions.value,
                    allowlist.value.split(',').map { it.trim() }.filter { it.isNotEmpty() },
                    blocklist.value.split(',').map { it.trim() }.filter { it.isNotEmpty() },
                )
                onBack()
            }, modifier = Modifier.padding(top = 16.dp)) {
                Text("Save and Back")
            }
        }
    }
}
