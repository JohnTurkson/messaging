package com.johnturkson.messaging.server.functions

import com.johnturkson.awstools.dynamodb.requestbuilder.requests.PutItemRequest
import com.johnturkson.messaging.common.data.Conversation
import com.johnturkson.messaging.common.data.ConversationData
import com.johnturkson.messaging.common.requests.CreateConversationRequest
import com.johnturkson.messaging.common.responses.CreateConversationResponse
import com.johnturkson.messaging.common.responses.Response
import com.johnturkson.messaging.server.configuration.DatabaseRequestHandler
import com.johnturkson.messaging.server.configuration.SerializerConfiguration
import com.johnturkson.messaging.server.lambda.WebsocketLambdaFunction
import com.johnturkson.messaging.server.lambda.WebsocketRequestContext
import kotlinx.coroutines.runBlocking
import kotlin.random.Random
import kotlin.random.nextInt

class CreateConversationFunction : WebsocketLambdaFunction<CreateConversationRequest, CreateConversationResponse> {
    override val serializer = SerializerConfiguration.instance
    override val inputSerializer = CreateConversationRequest.serializer()
    override val outputSerializer = Response.serializer()
    
    override fun processRequest(
        request: CreateConversationRequest,
        context: WebsocketRequestContext,
    ): CreateConversationResponse {
        return runBlocking {
            createConversation(generateConversationId(), request.data)
        }
    }
    
    fun generateConversationId(length: Int = 16): String {
        var id = ""
        repeat(length) { id += Random.nextInt(0..0xf).toString(0x10) }
        return id
    }
    
    suspend fun createConversation(id: String, data: ConversationData): CreateConversationResponse {
        // TODO check current user is contained in members
        // TODO check all members exist
        // TODO check user has permission to add member to conversation
        val conversation = Conversation(id, data.members)
        val table = "conversations"
        val request = PutItemRequest(
            tableName = table,
            item = conversation,
        )
        val response = DatabaseRequestHandler.instance.putItem(request, Conversation.serializer())
        return CreateConversationResponse(conversation)
    }
}
