package com.johnturkson.messaging.server.lambda

import com.amazonaws.services.lambda.runtime.Context
import com.johnturkson.messaging.server.responses.Response
import java.io.InputStream
import java.io.OutputStream

interface WebsocketLambdaFunction<T, R : Response> : AbstractLambdaFunction<T, Response> {
    override fun handleRequest(input: InputStream, output: OutputStream, context: Context) {
        LambdaFunctionRequestHandler.handleRequest(input, output, context) { request -> processInput(request) }
    }
    
    override fun processInput(input: String): String {
        val (request, context) = decodeInput(input)
        val response = processRequest(request, context)
        return encodeResponse(response)
    }
    
    fun processRequest(request: T, context: WebsocketRequestContext): R
    
    private fun decodeInput(input: String): Pair<T, WebsocketRequestContext> {
        val serializer = WebsocketRequest.serializer()
        val websocketRequest = configuration.decodeFromString(serializer, input)
        val request = configuration.decodeFromString(inputSerializer, websocketRequest.body)
        val context = websocketRequest.requestContext
        return Pair(request, context)
    }
    
    private fun encodeResponse(response: R): String {
        val serializer = WebsocketResponse.serializer()
        val websocketResponse = WebsocketResponse(configuration.encodeToString(outputSerializer, response))
        return configuration.encodeToString(serializer, websocketResponse)
    }
}
