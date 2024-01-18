package com.github.springeye.memosc.core
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate

expect fun Instant.formatDate(pattern: String, defValue: String = ""): String

expect fun String.parseDate(pattern: String, defValue: Long = 0L): Long