package com.github.springeye.memosc.model

import kotlinx.datetime.LocalDate
import kotlinx.datetime.toKotlinLocalDate
import java.time.DayOfWeek
import java.time.format.TextStyle
import java.time.temporal.WeekFields
import java.util.Locale

actual fun initialMatrix(): List<DailyUsageStat> {
    val now = java.time.LocalDate.now()
    return (1..now.lengthOfYear()).map { day ->
        DailyUsageStat(date = now.minusDays(day - 1L).toKotlinLocalDate())
    }.reversed()
}

actual val weekDays: List<String>
    get(){
        val day = WeekFields.of(Locale.getDefault()).firstDayOfWeek
        return DayOfWeek.values().mapIndexed { index, _ ->
            day.plus(index.toLong()).getDisplayName(TextStyle.SHORT, Locale.getDefault())
        }
    }

actual fun calculateMatrix(memos: Map<LocalDate, List<LocalDate>>): List<DailyUsageStat> {
    TODO("Not yet implemented")
}