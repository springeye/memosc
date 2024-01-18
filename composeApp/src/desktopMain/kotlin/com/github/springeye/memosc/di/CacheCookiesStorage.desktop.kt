package com.github.springeye.memosc.di

import com.github.springeye.memosc.AppPreferences
import io.ktor.client.plugins.cookies.CookiesStorage
import io.ktor.http.Cookie
import io.ktor.http.Url
import io.ktor.util.date.getTimeMillis
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.util.concurrent.atomic.AtomicLong
import kotlin.math.min


actual class PersistentCookiesStorage actual constructor(val store: AppPreferences) :
    CookiesStorage {
    override suspend fun addCookie(requestUrl: Url, cookie: Cookie) {
        val items = getCookiesFromStorage()
        items.removeAll { it.name == cookie.name && it.matches(requestUrl) }
        items.add(cookie.fillDefaults(requestUrl))
        store.setString("cookies", Json.encodeToString(items.map { it.toSaveString() }.toList()));
    }

    private suspend fun getCookiesFromStorage(): MutableList<Cookie> {
        val old = store.getString("cookies") ?: "[]"
        val items = Json.decodeFromString<MutableList<String>>(old)
        val cookies = mutableListOf<Cookie>()
        return cookies.apply {
            addAll(items.map { fromString(it) })
        };
    }
    private val oldestCookie: AtomicLong = AtomicLong(0L)
    override suspend fun get(requestUrl: Url): List<Cookie> {
        val now = getTimeMillis()
        if (now >= oldestCookie.get()) cleanup(now)
        val list = getCookiesFromStorage().filter {
            it.matches(requestUrl)
        }
        return list
    }
    private suspend fun cleanup(timestamp: Long) {
        val cookies = getCookiesFromStorage()
        cookies.removeAll { cookie ->
            val expires = cookie.expires?.timestamp ?: return@removeAll false
            expires < timestamp
        }

        val newOldest = cookies.fold(Long.MAX_VALUE) { acc, cookie ->
            cookie.expires?.timestamp?.let { min(acc, it) } ?: acc
        }

        oldestCookie.set(newOldest)
        store.setString("cookies", Json.encodeToString(cookies.map { it.toSaveString() }.toList()));
    }
    override fun close() {
    }
}

