package com.johnturkson.messaging.server.functions

import com.johnturkson.awstools.dynamodb.request.PutItemRequest
import com.johnturkson.awstools.signer.AWSRequestSigner.Header
import com.johnturkson.awstools.signer.AWSRequestSigner.generateRequestHeaders
import com.johnturkson.messaging.server.data.Connection
import com.johnturkson.messaging.server.data.Message
import com.johnturkson.messaging.server.data.MessageData
import com.johnturkson.messaging.server.lambda.WebsocketLambdaFunction
import com.johnturkson.messaging.server.lambda.WebsocketRequestContext
import com.johnturkson.messaging.server.requests.CreateMessageRequest
import com.johnturkson.messaging.server.responses.CreateMessageResponse
import com.johnturkson.messaging.server.responses.Response
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.net.URL
import kotlin.random.Random
import kotlin.random.nextInt

class CreateMessageFunction : WebsocketLambdaFunction<CreateMessageRequest, CreateMessageResponse> {
    override val configuration = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
    }
    override val inputSerializer = CreateMessageRequest.serializer()
    override val outputSerializer = Response.serializer()
    
    override fun processRequest(
        request: CreateMessageRequest,
        context: WebsocketRequestContext,
    ): CreateMessageResponse {
        val response = createMessage(request.data)
        val recipients = GetConnectionsFunction().getConnections()
        broadcastMessage(response.data, recipients.data)
        return response
    }
    
    fun generateMessageId(length: Int = 16): String {
        var id = ""
        repeat(length) { id += Random.nextInt(0..0xf).toString(0x10) }
        return id
    }
    
    fun createMessage(data: MessageData): CreateMessageResponse {
        val table = "messages"
        
        val id = generateMessageId()
        val message = Message(id, data.contents)
        val request = PutItemRequest(table, message)
        
        val accessKeyId = System.getenv("AWS_ACCESS_KEY_ID")
        val secretKey = System.getenv("AWS_SECRET_ACCESS_KEY")
        val sessionToken = System.getenv("AWS_SESSION_TOKEN")
        
        val region = "us-west-2"
        val service = "dynamodb"
        val method = "POST"
        val url = "https://dynamodb.us-west-2.amazonaws.com"
        
        val body = configuration.encodeToString(PutItemRequest.serializer(Message.serializer()), request)
        
        val headers = listOf(
            Header("X-Amz-Security-Token", sessionToken),
            Header("X-Amz-Target", "DynamoDB_20120810.PutItem"),
        )
        
        val signedHeaders = generateRequestHeaders(
            accessKeyId,
            secretKey,
            region,
            service,
            method,
            url,
            body,
            headers,
        )
        
        val call = Request.Builder()
            .url(URL(url))
            .apply { signedHeaders.forEach { (name, value) -> addHeader(name, value) } }
            .method(method, body.toRequestBody("application/json".toMediaType()))
            .build()
        
        // TODO make singleton client
        val client = OkHttpClient()
        
        client.newCall(call).execute().close()
        
        return CreateMessageResponse(message)
    }
    
    // TODO extract to separate function - BroadcastMessageFunction (and BroadcastMessageRequest/Response)
    fun broadcastMessage(message: Message, recipients: List<Connection>) {
        recipients.forEach { recipient ->
            val accessKeyId = System.getenv("AWS_ACCESS_KEY_ID")
            val secretKey = System.getenv("AWS_SECRET_ACCESS_KEY")
            val sessionToken = System.getenv("AWS_SESSION_TOKEN")
            
            val id = recipient.id.replace("=", "%3D")
            val region = "us-west-2"
            val service = "execute-api"
            val method = "POST"
            val url = "https://2od2rn13th.execute-api.us-west-2.amazonaws.com/default/%40connections/$id"
            
            val body = configuration.encodeToString(Message.serializer(), message)
            
            val headers = listOf(Header("X-Amz-Security-Token", sessionToken))
            
            val signedHeaders = generateRequestHeaders(
                accessKeyId,
                secretKey,
                region,
                service,
                method,
                url,
                body,
                headers,
            )
            
            val call = Request.Builder()
                .url(URL(url))
                .apply { signedHeaders.forEach { (name, value) -> addHeader(name, value) } }
                .method(method, body.toRequestBody("application/json".toMediaType()))
                .build()
            
            // TODO make singleton client
            val client = OkHttpClient()
            
            client.newCall(call).execute().close()
        }
    }
}
