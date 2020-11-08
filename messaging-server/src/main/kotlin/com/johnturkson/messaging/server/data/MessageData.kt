package com.johnturkson.messaging.server.data

import kotlinx.serialization.Serializable

@Serializable
data class MessageData(val sender: String, val conversation: String, val contents: String)
