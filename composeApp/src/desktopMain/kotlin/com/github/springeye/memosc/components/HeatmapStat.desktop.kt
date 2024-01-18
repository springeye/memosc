package com.github.springeye.memosc.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.springeye.memosc.model.DailyUsageStat
import kotlinx.datetime.toJavaLocalDate
import java.time.LocalDate

@Composable
actual fun HeatmapStat2(day: DailyUsageStat) {
    val date = day.date.toJavaLocalDate()
    val borderWidth = if (date == LocalDate.now()) 1.dp else 0.dp
    val color = when (day.count) {
        0 -> Color(0xffeaeaea)
        1 -> Color(0xff9be9a8)
        2 -> Color(0xff40c463)
        in 3..4 -> Color(0xff30a14e)
        else -> Color(0xff216e39)
    }
    var modifier = Modifier
        .fillMaxSize()
        .aspectRatio(1F, true)
        .clip(RoundedCornerShape(2.dp))
        .background(color = color)
    var text=""
    if (date == LocalDate.now()) {
        modifier = modifier.border(
            borderWidth,
            MaterialTheme.colorScheme.onBackground,
            shape = RoundedCornerShape(2.dp)
        )
//        text="${date.dayOfMonth}";
    }

    Box(modifier = modifier, contentAlignment = Alignment.Center){
        Text(text, textAlign = TextAlign.Center, style = MaterialTheme.typography.bodySmall.copy(fontSize = 6.sp ),)
    }
}