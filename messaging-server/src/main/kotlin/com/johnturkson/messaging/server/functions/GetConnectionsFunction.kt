package com.johnturkson.messaging.server.functions

import com.johnturkson.awstools.dynamodb.request.ScanRequest
import com.johnturkson.messaging.server.configuration.DatabaseRequestHandler
import com.johnturkson.messaging.server.configuration.SerializerConfiguration
import com.johnturkson.messaging.server.data.Connection
import com.johnturkson.messaging.server.lambda.WebsocketLambdaFunction
import com.johnturkson.messaging.server.lambda.WebsocketRequestContext
import com.johnturkson.messaging.server.requests.GetConnectionsRequest
import com.johnturkson.messaging.server.responses.GetConnectionsResponse
import com.johnturkson.messaging.server.responses.Response
import kotlinx.coroutines.runBlocking

class GetConnectionsFunction : WebsocketLambdaFunction<GetConnectionsRequest, GetConnectionsResponse> {
    override val serializer = SerializerConfiguration.instance
    override val inputSerializer = GetConnectionsRequest.serializer()
    override val outputSerializer = Response.serializer()
    
    override fun processRequest(
        request: GetConnectionsRequest,
        context: WebsocketRequestContext,
    ): GetConnectionsResponse {
        return runBlocking { GetConnectionsResponse(getConnections()) }
    }
    
    suspend fun getConnections(): List<Connection> {
        val table = "connections"
        val request = ScanRequest<Connection>(table)
        val response = DatabaseRequestHandler.instance.scan(request, Connection.serializer())
        return response.items
    }
}
