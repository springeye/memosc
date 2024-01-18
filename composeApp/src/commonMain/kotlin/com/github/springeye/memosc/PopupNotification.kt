package com.github.springeye.memosc
import androidx.compose.runtime.MutableState

interface PopupNotification {
     fun showPopUpMessage(text: String)
     fun showLoading(text: String="loading")
     fun hideLoading()
}
expect fun createPopupNotification(state:MutableState<ToastState>): PopupNotification