package com.johnturkson.messaging.server.functions

import com.johnturkson.awstools.dynamodb.requestbuilder.requests.PutItemRequest
import com.johnturkson.awstools.requesthandler.AWSRequestHandler
import com.johnturkson.awstools.requesthandler.AWSServiceConfiguration
import com.johnturkson.messaging.server.configuration.ClientConfiguration
import com.johnturkson.messaging.server.configuration.CredentialsConfiguration
import com.johnturkson.messaging.server.configuration.DatabaseRequestHandler
import com.johnturkson.messaging.server.configuration.SerializerConfiguration
import com.johnturkson.messaging.common.data.Connection
import com.johnturkson.messaging.common.data.Message
import com.johnturkson.messaging.common.data.MessageData
import com.johnturkson.messaging.server.lambda.WebsocketLambdaFunction
import com.johnturkson.messaging.server.lambda.WebsocketRequestContext
import com.johnturkson.messaging.common.requests.CreateMessageRequest
import com.johnturkson.messaging.common.responses.CreateMessageResponse
import com.johnturkson.messaging.common.responses.Response
import kotlinx.coroutines.runBlocking
import kotlin.random.Random
import kotlin.random.nextInt

class CreateMessageFunction : WebsocketLambdaFunction<CreateMessageRequest, CreateMessageResponse> {
    override val serializer = SerializerConfiguration.instance
    override val inputSerializer = CreateMessageRequest.serializer()
    override val outputSerializer = Response.serializer()
    
    override fun processRequest(
        request: CreateMessageRequest,
        context: WebsocketRequestContext,
    ): CreateMessageResponse {
        return runBlocking {
            val response = createMessage(generateMessageId(), generateMessageTime(), request.data)
            // TODO get all members of message conversation
            // TODO broadcast message to conversation participants
            // broadcastMessage(message, recipients)
            response
        }
    }
    
    fun generateMessageId(length: Int = 16): String {
        var id = ""
        repeat(length) { id += Random.nextInt(0..0xf).toString(0x10) }
        return id
    }
    
    fun generateMessageTime(): Long {
        return System.currentTimeMillis()
    }
    
    suspend fun createMessage(id: String, time: Long, data: MessageData): CreateMessageResponse {
        val message = Message(id, time, data.conversation, data.contents)
        val table = "messages"
        val request = PutItemRequest(
            tableName = table,
            item = message
        )
        val response = DatabaseRequestHandler.instance.putItem(request, Message.serializer())
        return CreateMessageResponse(message)
    }
    
    // TODO extract to separate function - BroadcastMessageFunction
    suspend fun broadcastMessage(message: Message, recipients: List<Connection>) {
        recipients.forEach { recipient ->
            val id = recipient.id.replace("=", "%3D")
            
            val region = "us-west-2"
            val service = "execute-api"
            val method = "POST"
            val url = "https://2od2rn13th.execute-api.us-west-2.amazonaws.com/default/%40connections/$id"
            
            // TODO encode type for broadcasted message
            val body = serializer.encodeToString(Message.serializer(), message)
            
            val serviceConfiguration = AWSServiceConfiguration(service, region, url, method)
            val handler = AWSRequestHandler(
                CredentialsConfiguration.instance,
                serviceConfiguration,
                ClientConfiguration.instance,
            )
            
            handler.request(body)
        }
    }
}
