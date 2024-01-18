import androidx.compose.runtime.MutableState

actual fun createPopupNotification(state: MutableState<ToastState>): PopupNotification {
//    TODO("Not yet implemented")
    return object:PopupNotification{
        override fun showPopUpMessage(text: String) {
        }

        override fun showLoading(text: String) {
        }

        override fun hideLoading() {
        }
    }
}