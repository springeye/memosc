package com.github.springeye.memosc.model
import kotlinx.serialization.Serializable


@Serializable
data class Profile(
    val mode: String,
    val version: String
)

@Serializable
data class Status(
    val profile: Profile
)