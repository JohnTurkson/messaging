package com.johnturkson.messaging.server.functions

import com.johnturkson.awstools.dynamodb.request.PutItemRequest
import com.johnturkson.awstools.signer.AWSRequestSigner
import com.johnturkson.messaging.server.data.Connection
import com.johnturkson.messaging.server.lambda.WebsocketLambdaFunction
import com.johnturkson.messaging.server.lambda.WebsocketRequestContext
import com.johnturkson.messaging.server.requests.CreateConnectionRequest
import com.johnturkson.messaging.server.responses.CreateConnectionResponse
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.net.URL

class CreateConnectionFunction : WebsocketLambdaFunction<CreateConnectionRequest, CreateConnectionResponse> {
    override val configuration = Json { ignoreUnknownKeys = true }
    override val inputSerializer = CreateConnectionRequest.serializer()
    override val outputSerializer = CreateConnectionResponse.serializer()
    
    override fun processRequest(
        request: CreateConnectionRequest,
        context: WebsocketRequestContext,
    ): CreateConnectionResponse {
        return createConnection(Connection(context.connectionId, request.data))
    }
    
    fun createConnection(connection: Connection): CreateConnectionResponse {
        val table = "connections"
        
        val request = PutItemRequest(table, connection)
        
        val accessKeyId = System.getenv("AWS_ACCESS_KEY_ID")
        val secretKey = System.getenv("AWS_SECRET_ACCESS_KEY")
        val sessionToken = System.getenv("AWS_SESSION_TOKEN")
        
        val region = "us-west-2"
        val service = "dynamodb"
        val method = "POST"
        val url = "https://dynamodb.us-west-2.amazonaws.com"
        
        val body = configuration.encodeToString(PutItemRequest.serializer(Connection.serializer()), request)
        
        val headers = listOf(
            AWSRequestSigner.Header("X-Amz-Security-Token", sessionToken),
            AWSRequestSigner.Header("X-Amz-Target", "DynamoDB_20120810.PutItem"),
        )
        
        val signedHeaders = AWSRequestSigner.generateRequestHeaders(
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
        
        return CreateConnectionResponse
    }
}
