package com.johnturkson.messaging.server.functions

import com.amazonaws.services.lambda.runtime.Context
import com.amazonaws.services.lambda.runtime.RequestStreamHandler
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json
import java.io.InputStream
import java.io.OutputStream

interface LambdaFunction<T, R> : RequestStreamHandler {
    val configuration: Json
    val inputSerializer: KSerializer<T>
    val outputSerializer: KSerializer<R>
    
    override fun handleRequest(input: InputStream, output: OutputStream, context: Context) {
        LambdaFunctionRequestStreamHandler(this).handleRequest(input, output, context)
    }
    
    fun processInput(input: String): String
}
