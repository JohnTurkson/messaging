package com.johnturkson.messaging.server.configuration

import com.johnturkson.awstools.dynamodb.requesthandler.DynamoDBRequestHandler

object DatabaseRequestHandler {
    val instance: DynamoDBRequestHandler = DynamoDBRequestHandler(
        DatabaseConfiguration.instance,
        CredentialsConfiguration.instance,
        ClientConfiguration.instance,
        SerializerConfiguration.instance,
    )
}
