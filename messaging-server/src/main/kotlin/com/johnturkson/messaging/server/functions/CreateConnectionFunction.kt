package com.johnturkson.messaging.server.functions

import com.johnturkson.awstools.dynamodb.requestbuilder.requests.PutItemRequest
import com.johnturkson.messaging.server.configuration.DatabaseRequestHandler
import com.johnturkson.messaging.server.configuration.SerializerConfiguration
import com.johnturkson.messaging.common.data.Connection
import com.johnturkson.messaging.common.data.ConnectionData
import com.johnturkson.messaging.server.lambda.WebsocketLambdaFunction
import com.johnturkson.messaging.server.lambda.WebsocketRequestContext
import com.johnturkson.messaging.common.requests.CreateConnectionRequest
import com.johnturkson.messaging.common.responses.CreateConnectionResponse
import com.johnturkson.messaging.common.responses.Response
import kotlinx.coroutines.runBlocking

class CreateConnectionFunction : WebsocketLambdaFunction<CreateConnectionRequest, CreateConnectionResponse> {
    override val serializer = SerializerConfiguration.instance
    override val inputSerializer = CreateConnectionRequest.serializer()
    override val outputSerializer = Response.serializer()
    
    override fun processRequest(
        request: CreateConnectionRequest,
        context: WebsocketRequestContext,
    ): CreateConnectionResponse {
        return runBlocking {
            createConnection(context.connectionId, request.data)
        }
    }
    
    suspend fun createConnection(id: String, data: ConnectionData): CreateConnectionResponse {
        val connection = Connection(id, data.user)
        val table = "connections"
        val request = PutItemRequest(
            tableName = table,
            item = connection
        )
        val response = DatabaseRequestHandler.instance.putItem(request, Connection.serializer())
        return CreateConnectionResponse(connection)
    }
}
