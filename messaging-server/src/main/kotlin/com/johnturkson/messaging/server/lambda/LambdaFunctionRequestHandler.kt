package com.johnturkson.messaging.server.lambda

import com.amazonaws.services.lambda.runtime.Context
import java.io.InputStream
import java.io.OutputStream

object LambdaFunctionRequestHandler {
    fun handleRequest(input: InputStream, output: OutputStream, context: Context, handler: (String) -> String) {
        val request = input.bufferedReader().use { reader -> reader.readText() }
        val response = handler(request)
        output.bufferedWriter().use { writer -> writer.write(response) }
    }
}
