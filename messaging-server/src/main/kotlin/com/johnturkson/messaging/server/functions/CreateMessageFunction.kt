package com.johnturkson.messaging.server.functions

import com.johnturkson.awstools.dynamodb.request.PutItemRequest
import com.johnturkson.awstools.requesthandler.AWSRequestHandler
import com.johnturkson.awstools.requesthandler.AWSServiceConfiguration
import com.johnturkson.messaging.server.configuration.ClientConfiguration
import com.johnturkson.messaging.server.configuration.CredentialsConfiguration
import com.johnturkson.messaging.server.configuration.DatabaseRequestHandler
import com.johnturkson.messaging.server.configuration.SerializerConfiguration
import com.johnturkson.messaging.server.data.Connection
import com.johnturkson.messaging.server.data.Message
import com.johnturkson.messaging.server.data.MessageData
import com.johnturkson.messaging.server.lambda.WebsocketLambdaFunction
import com.johnturkson.messaging.server.lambda.WebsocketRequestContext
import com.johnturkson.messaging.server.requests.CreateMessageRequest
import com.johnturkson.messaging.server.responses.CreateMessageResponse
import com.johnturkson.messaging.server.responses.Response
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
            val message = createMessage(request.data)
            // TODO get all members of message conversation
            val recipients = GetConnectionsFunction().getConnections()
            // TODO broadcast message to conversation participants
            // broadcastMessage(message, recipients)
            CreateMessageResponse(message)
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
    
    suspend fun createMessage(data: MessageData): Message {
        val id = generateMessageId()
        val time = generateMessageTime()
        val message = Message(id, time, data.conversation, data.contents)
        
        val table = "messages"
        val request = PutItemRequest(table, message)
        val body = serializer.encodeToString(PutItemRequest.serializer(Message.serializer()), request)
        
        // TODO make response return created message and return it
        val response = DatabaseRequestHandler.instance.putItem(request, Message.serializer())
        
        return message
    }
    
    // TODO extract to separate function - BroadcastMessageFunction (and BroadcastMessageRequest/Response)
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
