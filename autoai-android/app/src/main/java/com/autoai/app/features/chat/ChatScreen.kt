package com.autoai.app.features.chat

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun ChatScreen(onOpenSettings: () -> Unit, vm: ChatViewModel = viewModel()) {
    var input by remember { mutableStateOf(TextFieldValue("")) }
    val messages = remember { mutableStateListOf<Pair<Boolean, String>>() }

    Scaffold(topBar = {
        TopAppBar(
            title = { Text("AutoAI") },
            actions = { IconButton(onClick = onOpenSettings) { Icon(Icons.Filled.Settings, contentDescription = null) } }
        )
    }, bottomBar = {
        Row(Modifier.fillMaxWidth().padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
            OutlinedTextField(
                value = input,
                onValueChange = { input = it },
                modifier = Modifier.weight(1f),
                placeholder = { Text("Ask about Ford/Holden parts...") }
            )
            Spacer(Modifier.width(8.dp))
            Button(onClick = {
                val text = input.text.trim()
                if (text.isNotEmpty()) {
                    messages.add(true to text)
                    // Hook into VM which will use RAG orchestrator
                    messages.add(false to vm.answer(text))
                    input = TextFieldValue("")
                }
            }) { Text("Send") }
        }
    }) { padding ->
        LazyColumn(Modifier.fillMaxSize().padding(padding).padding(8.dp)) {
            items(messages) { (isUser, text) ->
                val bg = if (isUser) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant
                val align = if (isUser) Alignment.End else Alignment.Start
                Row(Modifier.fillMaxWidth(), horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start) {
                    Surface(color = bg, shape = MaterialTheme.shapes.medium, tonalElevation = 2.dp, modifier = Modifier.widthIn(max = 320.dp)) {
                        Text(text, modifier = Modifier.padding(12.dp))
                    }
                }
                Spacer(Modifier.height(8.dp))
            }
        }
    }
}
