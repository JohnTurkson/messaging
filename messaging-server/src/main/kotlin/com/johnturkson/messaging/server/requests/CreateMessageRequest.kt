package com.johnturkson.messaging.server.requests

import com.johnturkson.messaging.server.data.MessageData
import kotlinx.serialization.Serializable

@Serializable
data class CreateMessageRequest(val data: MessageData)
