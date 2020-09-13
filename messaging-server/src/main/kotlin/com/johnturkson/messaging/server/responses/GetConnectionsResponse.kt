package com.johnturkson.messaging.server.responses

import kotlinx.serialization.Serializable

@Serializable
data class GetConnectionsResponse(val connections: List<String>)
