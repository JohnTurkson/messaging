package com.johnturkson.messaging.server.responses

import com.johnturkson.messaging.server.data.Message
import kotlinx.serialization.Serializable

@Serializable
data class CreateMessageResponse(val data: Message)
