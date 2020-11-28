package com.johnturkson.messaging.common.data

import kotlinx.serialization.Serializable

@Serializable
data class Message(
    val id: String,
    val sender: String,
    val conversation: String,
    val contents: String,
    val created: Long,
    val modified: Long,
)
