package com.autoai.app.data

import android.app.Application
import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "settings")

data class UserSettings(
    val systemInstructions: String,
    val allowlist: List<String>,
    val blocklist: List<String>,
)

class SettingsStore(private val app: Application) {
    companion object {
        private val KEY_SYSTEM = stringPreferencesKey("system_instructions")
        private val KEY_ALLOW = stringPreferencesKey("allowlist")
        private val KEY_BLOCK = stringPreferencesKey("blocklist")
        private const val DEFAULT_ALLOW = "fordmods.com,boostcruising.com,fordforums.com.au,fordxr6turbo.com,justcommodores.com.au,ford.com.au,holden.com.au,rarespares.net.au"
        private const val DEFAULT_BLOCK = "ebay.com,ebay.com.au"
    }

    fun settingsFlow(): Flow<UserSettings> = app.dataStore.data.map { prefs ->
        UserSettings(
            systemInstructions = prefs[KEY_SYSTEM] ?: "Act as an automotive parts and diagnostics assistant for Ford Falcons and Holden Commodores. Cite sources. Abstain if unsure.",
            allowlist = (prefs[KEY_ALLOW] ?: DEFAULT_ALLOW).split(',').map { it.trim() }.filter { it.isNotEmpty() },
            blocklist = (prefs[KEY_BLOCK] ?: DEFAULT_BLOCK).split(',').map { it.trim() }.filter { it.isNotEmpty() },
        )
    }

    suspend fun save(settings: UserSettings) {
        app.dataStore.edit { prefs ->
            prefs[KEY_SYSTEM] = settings.systemInstructions
            prefs[KEY_ALLOW] = settings.allowlist.joinToString(",")
            prefs[KEY_BLOCK] = settings.blocklist.joinToString(",")
        }
    }
}
