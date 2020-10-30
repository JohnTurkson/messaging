package com.johnturkson.messaging.server.lambda

import kotlinx.serialization.Serializable

@Serializable
data class WebsocketResponse(
    val body: String,
    val statusCode: Int = 200,
)
