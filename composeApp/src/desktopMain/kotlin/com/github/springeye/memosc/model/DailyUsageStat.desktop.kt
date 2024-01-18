package com.github.springeye.memosc.model

import kotlinx.datetime.toKotlinLocalDate
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.format.TextStyle
import java.time.temporal.WeekFields
import java.util.Locale

actual fun initialMatrix(): List<DailyUsageStat> {
    var now = LocalDate.now()
    now=now.plusDays(15)
    val lengthOfYear = now.lengthOfYear()
    val reversed = (0..lengthOfYear).map { day ->
        val stat = DailyUsageStat(date = now.minusDays(day - 1L).toKotlinLocalDate())
        stat
    }.reversed()
    return reversed
}

actual val weekDays: List<String>
    get(){
        val day = WeekFields.of(Locale.getDefault()).firstDayOfWeek
        return DayOfWeek.entries.toTypedArray().mapIndexed { index, _ ->
            day.plus(index.toLong()).getDisplayName(TextStyle.NARROW, Locale.getDefault())
        }
    }

actual fun calculateMatrix(memos: Map<kotlinx.datetime.LocalDate, List<kotlinx.datetime.LocalDate>>): List<DailyUsageStat> {
//    val countMap = HashMap<kotlinx.datetime.LocalDate, Int>()
    val countMap=memos.mapValues { it.value.size }

//    for (memo in memos) {
//        val date = LocalDateTime.ofEpochSecond(memo.createdTs, 0, OffsetDateTime.now().offset).toLocalDate().toKotlinLocalDate()
//        countMap[date] = (countMap[date] ?: 0) + 1
//    }

    return initialMatrix().map {
        it.copy(count = countMap[it.date] ?: 0)
    }
}