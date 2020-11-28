package com.johnturkson.messaging.common.data

import kotlinx.serialization.Serializable

@Serializable
data class Conversation(val id: String, val name: String, val created: Long, val modified: Long)
