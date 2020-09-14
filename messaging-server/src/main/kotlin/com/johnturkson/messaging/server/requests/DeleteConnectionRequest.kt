package com.johnturkson.messaging.server.requests

import kotlinx.serialization.Serializable

@Serializable
data class DeleteConnectionRequest(val id: String)
