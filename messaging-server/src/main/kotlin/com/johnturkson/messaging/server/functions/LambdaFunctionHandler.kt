package com.johnturkson.messaging.server.functions

import com.amazonaws.services.lambda.runtime.Context
import com.amazonaws.services.lambda.runtime.RequestStreamHandler
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json
import java.io.InputStream
import java.io.OutputStream

interface LambdaFunctionHandler<T, R> : RequestStreamHandler {
    val configuration: Json
    val inputSerializer: KSerializer<T>
    val outputSerializer: KSerializer<R>
    
    fun processRequest(request: T): R
    
    override fun handleRequest(input: InputStream, output: OutputStream, context: Context) {
        val request = input.bufferedReader().use { reader -> reader.readText() }
        
        // TODO rename
        val requestJson = configuration.decodeFromString(inputSerializer, request)
        
        val response = processRequest(requestJson)
        
        // TODO rename
        val responseJson = configuration.encodeToString(outputSerializer, response)
        
        output.bufferedWriter().use { writer -> writer.write(responseJson) }
    }
}
