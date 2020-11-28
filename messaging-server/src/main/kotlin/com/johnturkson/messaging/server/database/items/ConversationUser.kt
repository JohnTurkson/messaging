package com.johnturkson.messaging.server.database.items

import kotlinx.serialization.Serializable

@Serializable
data class ConversationUser(val id: String, val user: String)
