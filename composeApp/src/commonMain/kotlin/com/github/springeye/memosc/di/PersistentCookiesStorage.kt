package com.github.springeye.memosc.di

import com.github.springeye.memosc.AppPreferences
import io.ktor.client.plugins.cookies.CookiesStorage
import io.ktor.http.Cookie
import io.ktor.http.CookieEncoding
import io.ktor.http.Url
import io.ktor.http.hostIsIp
import io.ktor.http.isSecure
import io.ktor.util.date.GMTDate
import io.ktor.util.toLowerCasePreservingASCIIRules

expect class PersistentCookiesStorage(store: AppPreferences):CookiesStorage
internal fun Cookie.matches(requestUrl: Url): Boolean {
    val domain = domain?.toLowerCasePreservingASCIIRules()?.trimStart('.')
        ?: error("Domain field should have the default value")

    val path = with(path) {
        val current = path ?: error("Path field should have the default value")
        if (current.endsWith('/')) current else "$path/"
    }

    val host = requestUrl.host.toLowerCasePreservingASCIIRules()
    val requestPath = let {
        val pathInRequest = requestUrl.encodedPath
        if (pathInRequest.endsWith('/')) pathInRequest else "$pathInRequest/"
    }

    if (host != domain && (hostIsIp(host) || !host.endsWith(".$domain"))) {
        return false
    }

    if (path != "/" &&
        requestPath != path &&
        !requestPath.startsWith(path)
    ) {
        return false
    }

    return !(secure && !requestUrl.protocol.isSecure())
}

internal fun Cookie.fillDefaults(requestUrl: Url): Cookie {
    var result = this

    if (result.path?.startsWith("/") != true) {
        result = result.copy(path = requestUrl.encodedPath)
    }

    if (result.domain.isNullOrBlank()) {
        result = result.copy(domain = requestUrl.host)
    }

    return result
}

fun fromString(string: String): Cookie {
    val strings = string.split(":")
    val name = strings[0];
    val value = strings[1];
    val encoding = strings[2];
    val maxAge = strings[3].toInt();
    val expires = strings[4].toLong();
    val domain = strings[5];
    val path = strings[6];
    val secure = strings[7].toBoolean();
    val httpOnly = strings[8].toBoolean();
    val extensions = strings[9].split(",").map {
        val keyValue = it.split("#")
        keyValue[0] to keyValue[1]
    }.toMap()
    return Cookie(
        name, value,
        CookieEncoding.valueOf(encoding), maxAge,
        GMTDate(expires), domain, path, secure, httpOnly, extensions,
    )
}

internal fun Cookie.toSaveString(): String {
    val cookie = this
    val extensions = extensions.map {
        "${it.key}#${it.value}"
    }.joinToString(",")
    val c =
        "${cookie.name}:${cookie.value}:${cookie.encoding}:${cookie.maxAge}:${cookie.expires?.timestamp}:${cookie.domain}:${cookie.path}:${cookie.secure}:${cookie.httpOnly}:$extensions"

    return c
}