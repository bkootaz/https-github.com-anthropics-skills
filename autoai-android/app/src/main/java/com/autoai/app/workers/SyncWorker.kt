package com.autoai.app.workers

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.autoai.app.core.crawl.Crawler
import com.autoai.app.data.SettingsStore
import com.autoai.app.data.db.AppDatabase
import com.autoai.app.data.db.DocumentEntity
import kotlinx.coroutines.flow.first

class SyncWorker(appContext: Context, params: WorkerParameters) : CoroutineWorker(appContext, params) {
    override suspend fun doWork(): Result {
        val store = SettingsStore(applicationContext as android.app.Application)
        val settings = store.settingsFlow().first()
        val crawler = Crawler(settings.allowlist.toSet(), settings.blocklist.toSet())
        val db = AppDatabase.getInstance(applicationContext)
        val dao = db.documentDao()

        // Seed URLs (expand later):
        val seeds = listOf(
            "https://www.fordmods.com/",
            "https://www.fordforums.com.au/",
            "https://www.fordxr6turbo.com/forum/",
            "https://forums.justcommodores.com.au/",
            "https://www.rarespares.net.au/",
            "https://www.ford.com.au/",
            "https://www.holden.com.au/"
        )

        for (url in seeds) {
            try {
                val page = crawler.fetch(url) ?: continue
                val entity = DocumentEntity(
                    url = page.url,
                    domain = page.domain,
                    title = page.title,
                    content = page.text,
                    embedding = null,
                    updatedAtEpochMs = System.currentTimeMillis()
                )
                dao.upsert(entity)
            } catch (e: Exception) {
                // ignore individual failures
            }
        }
        return Result.success()
    }
}
