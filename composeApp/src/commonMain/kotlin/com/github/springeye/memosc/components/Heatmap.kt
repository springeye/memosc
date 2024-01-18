package com.github.springeye.memosc.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import com.github.springeye.memosc.model.DailyUsageStat
import kotlinx.datetime.*

@Composable
expect fun aa()

@Composable
fun Heatmap(matrix:List<DailyUsageStat>) {

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(7),
            horizontalArrangement = Arrangement.spacedBy(2.dp, alignment = Alignment.End),
            verticalArrangement = Arrangement.spacedBy(2.dp),
            userScrollEnabled = false,
        ) {
            val countHeatmap = countHeatmap(constraints)
            matrix.takeLast(31).forEach {
                item(key = it.date) {
                    HeatmapStat2(day = it)
                }
            }
        }
    }
}
expect fun countHeatmap(constraints: Constraints): Int