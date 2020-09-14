package com.johnturkson.messaging.server.requests

import kotlinx.serialization.Serializable

@Serializable
data class WebsocketRequest(
    val requestContext: WebsocketRequestContext,
    val body: String,
)
