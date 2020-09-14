package com.johnturkson.messaging.server.requests

import kotlinx.serialization.Serializable

@Serializable
data class WebsocketRequestContext(val connectionId: String)
