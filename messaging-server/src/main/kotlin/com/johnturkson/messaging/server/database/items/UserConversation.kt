package com.johnturkson.messaging.server.database.items

import kotlinx.serialization.Serializable

@Serializable
data class UserConversation(val id: String, val conversation: String, val created: Long, val modified: Long)
