package com.johnturkson.messaging.server.data

import kotlinx.serialization.Serializable

@Serializable
data class ConversationData(val members: List<String>)
