package com.johnturkson.messaging.server.functions

import com.johnturkson.awstools.dynamodb.request.PutItemRequest
import com.johnturkson.awstools.signer.AWSRequestSigner
import com.johnturkson.messaging.server.requests.CreateConnectionRequest
import com.johnturkson.messaging.server.requests.WebsocketRequestContext
import com.johnturkson.messaging.server.responses.CreateConnectionResponse
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.net.URL

object CreateConnectionFunction : WebsocketLambdaFunction<CreateConnectionRequest, CreateConnectionResponse> {
    override val configuration = Json
    override val inputSerializer = CreateConnectionRequest.serializer()
    override val outputSerializer = CreateConnectionResponse.serializer()
    
    override fun processRequest(
        request: CreateConnectionRequest,
        context: WebsocketRequestContext,
    ): CreateConnectionResponse {
        return createConnection(context.connectionId)
    }
    
    private fun createConnection(id: String): CreateConnectionResponse {
        val table = "connections"
        
        val request = PutItemRequest(table, id)
        
        val accessKeyId = System.getenv("AWS_ACCESS_KEY_ID")
        val secretKey = System.getenv("AWS_SECRET_ACCESS_KEY")
        val region = "us-west-2"
        val service = "dynamodb"
        val method = "POST"
        val url = "https://dynamodb.us-west-2.amazonaws.com"
        
        val body = configuration.encodeToString(PutItemRequest.serializer(String.serializer()), request)
        
        val headers = listOf(AWSRequestSigner.Header("X-Amz-Target", "DynamoDB_20120810.PutItem"))
        
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
