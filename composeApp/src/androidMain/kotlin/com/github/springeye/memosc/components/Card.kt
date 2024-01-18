package com.github.springeye.memosc.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Preview
@Composable
fun PreviewCard() {
    Scaffold {
        Column(modifier = Modifier.padding(it).fillMaxSize().background(Color.Gray)) {
            CardItem(radius = 10.dp,
                hoverColor = Color.Blue,

                ) {
                Text("asdfasfas")
            }
        }
    }
}
@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun  CardItem(
    radius: Dp =15.dp,
    color: Color=Color.White,
    hoverColor: Color=color,
    borderColor: Color=color,
    hoverBorderColor: Color=color,
    borderWidth:Dp=0.dp,
    paddingValues: PaddingValues= PaddingValues(10.dp),
    modifier:Modifier = Modifier, content: @Composable() (BoxScope.() -> Unit)) {

    var background = Modifier.clip(RoundedCornerShape(radius))
        .background(color)

    Box(modifier = background
        .then(modifier)
        .border(borderWidth,borderColor,shape = RoundedCornerShape(radius))
        .padding(paddingValues),
        content = content)
}