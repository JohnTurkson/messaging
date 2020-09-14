package com.johnturkson.messaging.server.functions

import com.johnturkson.awstools.dynamodb.request.ScanRequest
import com.johnturkson.awstools.dynamodb.request.ScanResponse
import com.johnturkson.awstools.signer.AWSRequestSigner.Header
import com.johnturkson.awstools.signer.AWSRequestSigner.generateRequestHeaders
import com.johnturkson.messaging.server.data.Connection
import com.johnturkson.messaging.server.requests.GetConnectionsRequest
import com.johnturkson.messaging.server.requests.WebsocketRequestContext
import com.johnturkson.messaging.server.responses.GetConnectionsResponse
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.net.URL

object GetConnectionsFunction : WebsocketLambdaFunction<GetConnectionsRequest, GetConnectionsResponse> {
    override val configuration = Json
    override val inputSerializer = GetConnectionsRequest.serializer()
    override val outputSerializer = GetConnectionsResponse.serializer()
    
    override fun processRequest(
        request: GetConnectionsRequest,
        context: WebsocketRequestContext,
    ): GetConnectionsResponse {
        return getConnections()
    }
    
    private fun getConnections(): GetConnectionsResponse {
        val table = "connections"
        
        val request = ScanRequest<String>(table)
        
        val accessKeyId = System.getenv("AWS_ACCESS_KEY_ID")
        val secretKey = System.getenv("AWS_SECRET_ACCESS_KEY")
        val region = "us-west-2"
        val service = "dynamodb"
        val method = "POST"
        val url = "https://dynamodb.us-west-2.amazonaws.com"
        
        val body = configuration.encodeToString(ScanRequest.serializer(String.serializer()), request)
        
        val headers = listOf(Header("X-Amz-Target", "DynamoDB_20120810.Scan"))
        
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
        
        val response = client.newCall(call).execute().use { response ->
            val responseBody = response.body?.string() ?: throw Exception("Missing Scan response body")
            configuration.decodeFromString(ScanResponse.serializer(Connection.serializer()), responseBody)
        }
        
        return GetConnectionsResponse(response.items)
    }
}
