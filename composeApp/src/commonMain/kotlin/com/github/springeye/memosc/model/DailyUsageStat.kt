package com.github.springeye.memosc.model

import kotlinx.datetime.LocalDate
import kotlinx.datetime.toJavaLocalDate
import java.time.format.DateTimeFormatter

data class DailyUsageStat(
    //milliseconds
    val date: LocalDate,
    val count: Int = 0
) {
    override fun toString(): String {
        return date.toJavaLocalDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
    }
}
expect fun initialMatrix(): List<DailyUsageStat>


expect val weekDays:List<String>
expect fun calculateMatrix(memos: Map<LocalDate, List<LocalDate>>): List<DailyUsageStat>