package com.johnturkson.messaging.server.configuration

import com.johnturkson.awstools.requesthandler.AWSCredentials

object CredentialsConfiguration {
    val instance: AWSCredentials = AWSCredentials(
        System.getenv("AWS_ACCESS_KEY_ID"),
        System.getenv("AWS_SECRET_ACCESS_KEY"),
        System.getenv("AWS_SESSION_TOKEN"),
    )
}
