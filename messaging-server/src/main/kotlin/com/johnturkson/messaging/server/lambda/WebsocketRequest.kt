package com.johnturkson.messaging.server.lambda

import kotlinx.serialization.Serializable

@Serializable
data class WebsocketRequest(
    val requestContext: WebsocketRequestContext,
    val body: String,
)
