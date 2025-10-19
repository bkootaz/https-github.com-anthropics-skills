package com.autoai.app

import android.app.Application
import androidx.work.Configuration

class AutoAiApp : Application(), Configuration.Provider {
    override fun getWorkManagerConfiguration(): Configuration =
        Configuration.Builder().setMinimumLoggingLevel(android.util.Log.INFO).build()
}
