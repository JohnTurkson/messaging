package com.johnturkson.messaging.server.requests

import com.johnturkson.messaging.server.data.ConnectionData
import kotlinx.serialization.Serializable

@Serializable
data class DeleteConnectionRequest(val data: ConnectionData)
