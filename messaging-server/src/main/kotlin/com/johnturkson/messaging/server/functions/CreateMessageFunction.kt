package com.johnturkson.messaging.server.functions

import com.johnturkson.awstools.dynamodb.requestbuilder.requests.PutItemRequest
import com.johnturkson.awstools.requesthandler.AWSServiceConfiguration
import com.johnturkson.awstools.requesthandler.DefaultAWSRequestHandler
import com.johnturkson.messaging.common.data.Connection
import com.johnturkson.messaging.common.data.Message
import com.johnturkson.messaging.common.data.MessageData
import com.johnturkson.messaging.common.requests.Request.CreateMessageRequest
import com.johnturkson.messaging.common.responses.Response
import com.johnturkson.messaging.common.responses.Response.CreateMessageResponse
import com.johnturkson.messaging.server.configuration.ClientConfiguration
import com.johnturkson.messaging.server.configuration.CredentialsConfiguration
import com.johnturkson.messaging.server.configuration.DatabaseRequestHandler
import com.johnturkson.messaging.server.configuration.SerializerConfiguration
import com.johnturkson.messaging.server.lambda.WebsocketLambdaFunction
import com.johnturkson.messaging.server.lambda.WebsocketRequestContext
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
            val id = generateMessageId()
            val time = generateMessageTime()
            val response = createMessage(id, request.data, time)
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
    
    suspend fun createMessage(id: String, data: MessageData, time: Long): CreateMessageResponse {
        val message = Message(id, data.sender, data.conversation, data.contents, time, time)
        val request = PutItemRequest(
            tableName = "Messages",
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
            val path = "https://2od2rn13th.execute-api.us-west-2.amazonaws.com"
            val service = "execute-api"
            val endpoint = "default/%40connections/$id"
            val url = "$path/$endpoint"
            val method = "POST"
            
            // TODO encode type for broadcasted message
            val handler = DefaultAWSRequestHandler(CredentialsConfiguration.instance, ClientConfiguration.instance)
            val serviceConfiguration = AWSServiceConfiguration(region, path, service, endpoint, url, method)
            val body = serializer.encodeToString(Message.serializer(), message)
            handler.request(serviceConfiguration, body)
        }
    }
}
