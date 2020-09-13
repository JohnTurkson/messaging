package com.johnturkson.messaging.server.data

import kotlinx.serialization.Serializable

@Serializable
data class Message(val id: String, val contents: String)
