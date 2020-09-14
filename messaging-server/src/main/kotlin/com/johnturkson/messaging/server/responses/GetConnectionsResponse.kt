package com.johnturkson.messaging.server.responses

import com.johnturkson.messaging.server.data.Connection
import kotlinx.serialization.Serializable

@Serializable
data class GetConnectionsResponse(val connections: List<Connection>)
