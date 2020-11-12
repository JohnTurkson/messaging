package com.johnturkson.messaging.server.configuration

import kotlinx.serialization.json.Json

object SerializerConfiguration {
    val instance: Json = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
    }
}
