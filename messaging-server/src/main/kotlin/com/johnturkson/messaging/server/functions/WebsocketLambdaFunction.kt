package com.johnturkson.messaging.server.functions

import com.johnturkson.messaging.server.requests.WebsocketRequest
import com.johnturkson.messaging.server.requests.WebsocketRequestContext
import kotlinx.serialization.json.Json

interface WebsocketLambdaFunction<T, R> : LambdaFunction<T, R> {
    override fun processInput(input: String): String {
        val (request, context) = decodeInput(input)
        val response = processRequest(request, context)
        return encodeResponse(response)
    }
    
    fun processRequest(request: T, context: WebsocketRequestContext): R
    
    private fun decodeInput(input: String): Pair<T, WebsocketRequestContext> {
        val decoder = Json { ignoreUnknownKeys = true }
        val serializer = WebsocketRequest.serializer()
        val websocketRequest = decoder.decodeFromString(serializer, input)
        val request = configuration.decodeFromString(inputSerializer, websocketRequest.body)
        val context = websocketRequest.requestContext
        return Pair(request, context)
    }
    
    private fun encodeResponse(response: R): String {
        return configuration.encodeToString(outputSerializer, response)
    }
}
