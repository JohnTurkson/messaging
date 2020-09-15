package com.johnturkson.messaging.server.lambda

import com.amazonaws.services.lambda.runtime.Context
import java.io.InputStream
import java.io.OutputStream

object LambdaFunctionRequestHandler {
    fun handleRequest(input: InputStream, output: OutputStream, context: Context, processor: (String) -> String) {
        val request = input.bufferedReader().use { reader -> reader.readText() }
        val response = processor(request)
        output.bufferedWriter().use { writer -> writer.write(response) }
    }
}
