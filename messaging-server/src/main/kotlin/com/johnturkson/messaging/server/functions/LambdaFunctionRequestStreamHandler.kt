package com.johnturkson.messaging.server.functions

import com.amazonaws.services.lambda.runtime.Context
import com.amazonaws.services.lambda.runtime.RequestStreamHandler
import java.io.InputStream
import java.io.OutputStream

class LambdaFunctionRequestStreamHandler<T, R>(private val function: LambdaFunction<T, R>) : RequestStreamHandler {
    override fun handleRequest(input: InputStream, output: OutputStream, context: Context) {
        val request = input.bufferedReader().use { reader -> reader.readText() }
        val response = function.processInput(request)
        output.bufferedWriter().use { writer -> writer.write(response) }
    }
}
