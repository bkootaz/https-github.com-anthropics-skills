package com.autoai.app.core.crawl

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup
import java.net.URL
import java.net.HttpURLConnection

class Crawler(private val allowDomains: Set<String>, private val blockDomains: Set<String>) {
    suspend fun fetch(url: String): Page? = withContext(Dispatchers.IO) {
        val domain = URL(url).host.removePrefix("www.")
        if (!isAllowed(domain)) return@withContext null
        // Basic robots.txt check (permit if robots not accessible)
        if (!isRobotsAllowed(domain, url)) return@withContext null
        val doc = Jsoup.connect(url).userAgent("AutoAI/0.1").timeout(15000).get()
        val title = doc.title()
        val text = doc.select("body").text()
        Page(url, domain, title, text)
    }

    private fun isAllowed(domain: String): Boolean {
        if (blockDomains.any { domain.endsWith(it) }) return false
        return allowDomains.any { domain.endsWith(it) }
    }

    private fun isRobotsAllowed(domain: String, url: String): Boolean {
        return try {
            val robotsUrl = "https://$domain/robots.txt"
            val conn = URL(robotsUrl).openConnection() as HttpURLConnection
            conn.connectTimeout = 5000
            conn.readTimeout = 5000
            conn.instanceFollowRedirects = true
            val code = conn.responseCode
            if (code in 200..299) {
                val content = conn.inputStream.bufferedReader().use { it.readText() }
                // Very naive Disallow check
                val path = URL(url).path
                val disallows = content.lines().map { it.trim() }.filter { it.startsWith("Disallow:") }
                    .map { it.removePrefix("Disallow:").trim() }
                disallows.none { d -> d.isNotEmpty() && path.startsWith(d) }
            } else true
        } catch (e: Exception) { true }
    }
}

data class Page(val url: String, val domain: String, val title: String, val text: String)
