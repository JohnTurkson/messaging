package com.johnturkson.messaging.common.data

import kotlinx.serialization.Serializable

@Serializable
data class Connection(val id: String, val user: String)
