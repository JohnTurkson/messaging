package com.johnturkson.messaging.server.database.keys

import kotlinx.serialization.Serializable

@Serializable
data class UserConversationKey(val id: String, val conversation: String)
