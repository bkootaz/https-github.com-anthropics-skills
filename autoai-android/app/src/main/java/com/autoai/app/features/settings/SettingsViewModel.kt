package com.autoai.app.features.settings

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.autoai.app.data.SettingsStore
import com.autoai.app.data.UserSettings
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class SettingsViewModel(app: Application) : AndroidViewModel(app) {
    private val store = SettingsStore(app)

    suspend fun loadSettings(): UserSettings = store.settingsFlow().first()

    fun saveSettings(system: String, allow: List<String>, block: List<String>) {
        viewModelScope.launch(Dispatchers.IO) {
            store.save(UserSettings(system, allow, block))
        }
    }
}
