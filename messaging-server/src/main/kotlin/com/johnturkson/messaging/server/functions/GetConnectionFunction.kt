package com.johnturkson.messaging.server.functions

import com.johnturkson.awstools.dynamodb.objectbuilder.buildDynamoDBObject
import com.johnturkson.awstools.dynamodb.requestbuilder.requests.GetItemRequest
import com.johnturkson.messaging.common.data.Connection
import com.johnturkson.messaging.common.requests.Request.GetConnectionRequest
import com.johnturkson.messaging.common.responses.Response
import com.johnturkson.messaging.common.responses.Response.GetConnectionResponse
import com.johnturkson.messaging.server.configuration.DatabaseRequestHandler
import com.johnturkson.messaging.server.configuration.SerializerConfiguration
import com.johnturkson.messaging.server.lambda.WebsocketLambdaFunction
import com.johnturkson.messaging.server.lambda.WebsocketRequestContext
import kotlinx.coroutines.runBlocking

class GetConnectionFunction : WebsocketLambdaFunction<GetConnectionRequest, GetConnectionResponse> {
    override val serializer = SerializerConfiguration.instance
    override val inputSerializer = GetConnectionRequest.serializer()
    override val outputSerializer = Response.serializer()
    
    override fun processRequest(
        request: GetConnectionRequest,
        context: WebsocketRequestContext,
    ): GetConnectionResponse {
        return runBlocking {
            getConnection(request.id)
        }
    }
    
    suspend fun getConnection(id: String): GetConnectionResponse {
        val table = "connections"
        val request = GetItemRequest(
            tableName = table,
            key = buildDynamoDBObject {
                put("id", id)
            }
        )
        val response = DatabaseRequestHandler.instance.getItem(request, Connection.serializer())
        return GetConnectionResponse(response.item)
    }
}
