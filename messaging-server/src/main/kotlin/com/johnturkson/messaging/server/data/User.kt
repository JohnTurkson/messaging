package com.johnturkson.messaging.server.data

import kotlinx.serialization.Serializable

@Serializable
data class User(val id: String, val username: String, val email: String)
