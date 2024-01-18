package com.github.springeye.memosc.model
import kotlinx.serialization.Serializable

@Serializable
data class ErrorMessage(
    val error: String? = null,
    val message: String
)