package com.github.springeye.memosc.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class MemosRole {
    HOST,
    USER
}

@Serializable
enum class MemosUserSettingKey {
    @SerialName("locale")
    LOCALE,
    @SerialName("memo-visibility")
    MEMO_VISIBILITY,
    @SerialName("editorFontStyle")
    EDITOR_FONT_STYLE,
    UNKNOWN
}

@Serializable
data class MemosUserSetting(
    val key: MemosUserSettingKey = MemosUserSettingKey.UNKNOWN,
    val value: String
)

@Serializable
data class User(
    val createdTs: Long,
    val email: String?,
    val id: Long,
    val name: String?,
    val role: MemosRole = MemosRole.USER,
    val rowStatus: MemosRowStatus = MemosRowStatus.NORMAL,
    val updatedTs: Long?,
    val userSettingList: List<MemosUserSetting>? = null,
    val nickname: String?,
    val username: String?,
    val avatarUrl: String?,
) {
    val displayEmail get() = email ?: username ?: ""
    val displayName get() = nickname ?: name ?: ""

    val memoVisibility: MemosVisibility
        get() = userSettingList?.find { it.key == MemosUserSettingKey.MEMO_VISIBILITY }?.let {
        try {
            MemosVisibility.valueOf(it.value.removePrefix("\"").removeSuffix("\""))
        } catch (_: IllegalArgumentException) {
            MemosVisibility.PRIVATE
        }
    } ?: MemosVisibility.PRIVATE
}