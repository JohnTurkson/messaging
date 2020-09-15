package com.johnturkson.messaging.server.lambda

import kotlinx.serialization.Serializable

@Serializable
data class WebsocketRequestContext(val connectionId: String)
