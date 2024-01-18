package com.github.springeye.memosc.core
import kotlinx.datetime.Instant
import java.text.SimpleDateFormat
import java.util.Date

actual fun Instant.formatDate(
    pattern: String,
    defValue: String
): String {
    return try {
        SimpleDateFormat(pattern).format(Date(this.toEpochMilliseconds()))
    } catch (e: Exception) {
        defValue
    }
}

actual fun String.parseDate(pattern: String, defValue: Long): Long {
    return try {
        SimpleDateFormat(pattern).parse(this).time
    } catch (e: Exception) {
        defValue
    }
}