package com.johnturkson.messaging.server.requests

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
class GetConnectionsRequest()


fun main() {
    Json.encodeToString(GetConnectionsRequest.serializer(), GetConnectionsRequest())
}
