package com.johnturkson.messaging.server.functions

import com.johnturkson.awstools.dynamodb.request.PutItemRequest
import com.johnturkson.messaging.server.configuration.DatabaseRequestHandler
import com.johnturkson.messaging.server.configuration.SerializerConfiguration
import com.johnturkson.messaging.server.data.Connection
import com.johnturkson.messaging.server.lambda.WebsocketLambdaFunction
import com.johnturkson.messaging.server.lambda.WebsocketRequestContext
import com.johnturkson.messaging.server.requests.CreateConnectionRequest
import com.johnturkson.messaging.server.responses.CreateConnectionResponse
import com.johnturkson.messaging.server.responses.Response
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json

class CreateConnectionFunction : WebsocketLambdaFunction<CreateConnectionRequest, CreateConnectionResponse> {
    override val serializer = SerializerConfiguration.instance
    override val inputSerializer = CreateConnectionRequest.serializer()
    override val outputSerializer = Response.serializer()
    
    override fun processRequest(
        request: CreateConnectionRequest,
        context: WebsocketRequestContext,
    ): CreateConnectionResponse {
        return runBlocking {
            CreateConnectionResponse(createConnection(Connection(context.connectionId, request.data.user)))
        }
    }
    
    suspend fun createConnection(connection: Connection): Connection {
        val table = "connections"
        val request = PutItemRequest(table, connection)
        // TODO make response return created connection and return it
        val response = DatabaseRequestHandler.instance.putItem(request, Connection.serializer())
        
        return connection
    }
}
