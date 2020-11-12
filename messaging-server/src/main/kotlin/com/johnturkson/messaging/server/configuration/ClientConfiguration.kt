package com.johnturkson.messaging.server.configuration

import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.features.websocket.WebSockets

object ClientConfiguration {
    val instance: HttpClient = HttpClient(CIO) {
        install(WebSockets)
    }
}
