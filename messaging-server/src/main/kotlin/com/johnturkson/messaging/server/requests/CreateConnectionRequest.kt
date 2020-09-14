package com.johnturkson.messaging.server.requests

import com.johnturkson.messaging.server.data.Connection
import kotlinx.serialization.Serializable

@Serializable
data class CreateConnectionRequest(val data: Connection)
