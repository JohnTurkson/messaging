package com.johnturkson.messaging.server.functions

import com.johnturkson.awstools.dynamodb.objectbuilder.buildDynamoDBObject
import com.johnturkson.awstools.dynamodb.request.GetItemRequest
import com.johnturkson.messaging.server.configuration.DatabaseRequestHandler
import com.johnturkson.messaging.server.configuration.SerializerConfiguration
import com.johnturkson.messaging.server.data.Connection
import com.johnturkson.messaging.server.lambda.WebsocketLambdaFunction
import com.johnturkson.messaging.server.lambda.WebsocketRequestContext
import com.johnturkson.messaging.server.requests.GetConnectionRequest
import com.johnturkson.messaging.server.responses.GetConnectionResponse
import com.johnturkson.messaging.server.responses.Response
import kotlinx.coroutines.runBlocking

class GetConnectionFunction : WebsocketLambdaFunction<GetConnectionRequest, GetConnectionResponse> {
    override val serializer = SerializerConfiguration.instance
    override val inputSerializer = GetConnectionRequest.serializer()
    override val outputSerializer = Response.serializer()
    
    override fun processRequest(
        request: GetConnectionRequest,
        context: WebsocketRequestContext,
    ): GetConnectionResponse {
        return runBlocking { GetConnectionResponse(getConnection(request.id)) }
    }
    
    suspend fun getConnection(id: String): Connection {
        val table = "connections"
        val request = GetItemRequest<Connection>(
            tableName = table,
            key = buildDynamoDBObject {
                put("id", id)
            }
        )
        
        val response = DatabaseRequestHandler.instance.getItem(request, Connection.serializer())
        
        return response.item
    }
}
