package com.johnturkson.messaging.server.functions

import com.johnturkson.awstools.dynamodb.objectbuilder.buildDynamoDBObject
import com.johnturkson.awstools.dynamodb.requestbuilder.requests.UpdateItemRequest
import com.johnturkson.awstools.ses.requestbuilder.builder.Content
import com.johnturkson.awstools.ses.requestbuilder.builder.Destination
import com.johnturkson.awstools.ses.requestbuilder.requests.SendEmailRequest
import com.johnturkson.messaging.common.data.User
import com.johnturkson.messaging.common.data.UserVerificationLink
import com.johnturkson.messaging.server.configuration.DatabaseRequestHandler
import com.johnturkson.messaging.server.configuration.EmailRequestHandler

// TODO regular lambda function
class SendUserVerificationLinkFunction {
    fun generateVerificationLink(user: User): String {
        // TODO generate link based on user id
        return ""
    }
    
    suspend fun sendVerificationLink(user: User) {
        val link = generateVerificationLink(user)
        val addUserVerificationLink = UpdateItemRequest(
            tableName = "UserVerificationLinks",
            key = buildDynamoDBObject {
                put("id", user.id)
            },
            updateExpression = "set link = :link",
            expressionAttributeValues = buildDynamoDBObject {
                put(":link", link)
            },
        )
        
        val response = DatabaseRequestHandler.instance.updateItem(
            addUserVerificationLink,
            UserVerificationLink.serializer()
        )
        
        val sender = "messaging@johnturkson.com"
        val email = SendEmailRequest(
            sender,
            Destination {
                ToAddresses(user.email)
            },
            Content {
                Simple {
                    Body {
                        Text {
                            Data("[TEST] [Verification link: $link]")
                        }
                    }
                    Subject {
                        Data("[TEST] Verify Email")
                    }
                }
            },
        )
        EmailRequestHandler.instance.sendEmail(email)
    }
}
