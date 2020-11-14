package com.johnturkson.messaging.common.data

import kotlinx.serialization.Serializable

@Serializable
data class Conversation(val id: String, val users: List<String>)
