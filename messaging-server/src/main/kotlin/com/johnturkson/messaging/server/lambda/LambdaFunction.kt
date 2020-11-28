package com.johnturkson.messaging.server.lambda

import com.amazonaws.services.lambda.runtime.Context
import com.amazonaws.services.lambda.runtime.RequestStreamHandler
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json
import java.io.InputStream
import java.io.OutputStream

interface LambdaFunction<T, R> : RequestStreamHandler {
    val serializer: Json
    val inputSerializer: KSerializer<T>
    val outputSerializer: KSerializer<R>
    
    override fun handleRequest(input: InputStream, output: OutputStream, context: Context) {
        val request = input.bufferedReader().use { reader -> reader.readText() }
        val response = processInput(request)
        output.bufferedWriter().use { writer -> writer.write(response) }
    }
    
    fun processInput(input: String): String
}
