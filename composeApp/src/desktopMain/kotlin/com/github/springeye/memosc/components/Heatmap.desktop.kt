package com.github.springeye.memosc.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.Constraints
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import java.time.temporal.TemporalAdjusters
import java.time.temporal.WeekFields
import java.util.Locale
import kotlin.math.ceil

actual fun countHeatmap(constraints: Constraints): Int {
    val cellSize = ceil(constraints.maxWidth.toDouble() / 7).toInt()
    if (cellSize <= 0) {
        return 0
    }
    val rows = constraints.maxHeight / cellSize
    val fullCells = rows * 7

    val firstDayOfWeek = WeekFields.of(Locale.getDefault()).firstDayOfWeek
    val firstDayOfThisWeek = LocalDate.now().with(TemporalAdjusters.previousOrSame(firstDayOfWeek))
    val lastColumn = ChronoUnit.DAYS.between(firstDayOfThisWeek, LocalDate.now()).toInt() + 1
    if (lastColumn % 7 == 0) {
        return fullCells
    }
    return fullCells - 7 + lastColumn
}

@Composable
actual fun aa() {
}