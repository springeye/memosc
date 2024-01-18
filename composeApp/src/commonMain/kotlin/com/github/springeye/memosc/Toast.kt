package com.github.springeye.memosc
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import kotlinx.coroutines.delay

sealed interface ToastState {
    object Hidden : ToastState
    class Shown(val message: String) : ToastState
    class Loading(val message: String) : ToastState

}

@Composable
fun Toast(
    state: MutableState<ToastState>
) {
    val value = state.value
    if (value is ToastState.Shown) {
        Box(
            modifier = Modifier.fillMaxSize().padding(bottom = 20.dp),
            contentAlignment = Alignment.BottomCenter
        ) {
            Surface(
                modifier = Modifier.size(300.dp, 70.dp),
                color = Color(23, 23, 23),
                shape = RoundedCornerShape(4.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(value.message, color = Color.White)
                }

                LaunchedEffect(value.message) {
                    delay(3000)
                    state.value = ToastState.Hidden
                }
            }
        }
    } else if (value is ToastState.Loading) {
        Dialog(onDismissRequest = {
            state.value = ToastState.Hidden
        }) {
//            CircularProgressIndicator(
//                modifier = Modifier.width(64.dp).wrapContentSize(Alignment.Center),
//                color = MaterialTheme.colorScheme.secondary,
//            )
            LoadingAnimation()
        }
//        Box(
//            modifier = Modifier.fillMaxSize().background(Color.Gray),
//            contentAlignment = Alignment.Center
//        ) {
//            Surface(
//                modifier = Modifier.size(70.dp, 70.dp),
//                color = Color(23, 23, 23),
//                shape = RoundedCornerShape(4.dp)
//            ) {
//                Box(contentAlignment = Alignment.Center,modifier = Modifier.fillMaxSize() ) {
//                    CircularProgressIndicator(
//                        modifier = Modifier.width(64.dp).wrapContentSize(Alignment.Center),
//                        color = MaterialTheme.colors.secondary,
//                        )
//                }
//            }
//        }
    }
}
