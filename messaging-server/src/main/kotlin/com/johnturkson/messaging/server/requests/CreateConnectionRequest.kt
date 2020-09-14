package com.johnturkson.messaging.server.requests

import kotlinx.serialization.Serializable

@Serializable
data class CreateConnectionRequest(val id: String)
