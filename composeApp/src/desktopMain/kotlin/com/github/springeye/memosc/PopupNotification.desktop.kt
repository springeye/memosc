package com.github.springeye.memosc


import androidx.compose.runtime.MutableState

actual fun createPopupNotification(state: MutableState<ToastState>): PopupNotification {
    return object: PopupNotification {
        override fun showPopUpMessage(text: String) {
            state.value= ToastState.Shown(text)
        }

        override fun showLoading(text: String) {
            state.value= ToastState.Loading(text)
        }

        override fun hideLoading() {
            state.value= ToastState.Hidden
        }
    }
}