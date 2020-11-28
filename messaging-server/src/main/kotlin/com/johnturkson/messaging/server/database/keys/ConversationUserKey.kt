package com.johnturkson.messaging.server.database.keys

import kotlinx.serialization.Serializable

@Serializable
data class ConversationUserKey(val id: String, val user: String)
