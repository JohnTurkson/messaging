package com.johnturkson.messaging.server.functions

import com.johnturkson.awstools.dynamodb.objectbuilder.buildDynamoDBObject
import com.johnturkson.awstools.dynamodb.requestbuilder.requests.DeleteItemRequest
import com.johnturkson.messaging.common.data.Connection
import com.johnturkson.messaging.common.requests.Request.DeleteConnectionRequest
import com.johnturkson.messaging.common.responses.Response
import com.johnturkson.messaging.common.responses.Response.DeleteConnectionResponse
import com.johnturkson.messaging.server.configuration.DatabaseRequestHandler
import com.johnturkson.messaging.server.configuration.SerializerConfiguration
import com.johnturkson.messaging.server.lambda.WebsocketLambdaFunction
import com.johnturkson.messaging.server.lambda.WebsocketRequestContext
import kotlinx.coroutines.runBlocking

class DeleteConnectionFunction : WebsocketLambdaFunction<DeleteConnectionRequest, DeleteConnectionResponse> {
    override val serializer = SerializerConfiguration.instance
    override val inputSerializer = DeleteConnectionRequest.serializer()
    override val outputSerializer = Response.serializer()
    
    override fun processRequest(
        request: DeleteConnectionRequest,
        context: WebsocketRequestContext,
    ): DeleteConnectionResponse {
        return runBlocking {
            deleteConnection(request.id)
        }
    }
    
    suspend fun deleteConnection(id: String): DeleteConnectionResponse {
        val table = "connections"
        val request = DeleteItemRequest(
            tableName = table,
            key = buildDynamoDBObject {
                put("id", id)
            }
        )
        DatabaseRequestHandler.instance.deleteItem(request, Connection.serializer())
        return DeleteConnectionResponse
    }
}
