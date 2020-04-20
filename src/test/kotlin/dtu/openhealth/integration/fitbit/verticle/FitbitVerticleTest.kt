package dtu.openhealth.integration.fitbit.verticle

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import dtu.openhealth.integration.fitbit.FitbitVerticle
import dtu.openhealth.integration.shared.model.ThirdPartyNotification
import dtu.openhealth.integration.shared.service.ThirdPartyNotificationService
import io.vertx.junit5.VertxExtension
import io.vertx.junit5.VertxTestContext
import io.vertx.reactivex.core.Vertx
import io.vertx.reactivex.core.buffer.Buffer
import io.vertx.reactivex.ext.web.client.HttpResponse
import io.vertx.reactivex.ext.web.client.WebClient
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(VertxExtension::class)
class FitbitVerticleTest {

    val validJsonString ="""
    [{
	    "collectionType": "activities",
	    "date": "2020-03-05",
	    "ownerId": "User123",
	    "ownerType": "user",
	    "subscriptionId": "2345"
    }]"""

    @Test
    fun testValidRequestBody(vertx: Vertx, testContext: VertxTestContext) {
        val notificationService : ThirdPartyNotificationService = mock()
        val parameterMap = mapOf(Pair("collectionType", "activities"),
            Pair("date", "2020-03-05"),
            Pair("ownerId", "User123"),
            Pair("ownerType", "user"),
            Pair("subscriptionId", "2345"))
        val expectedNotificationList = listOf(ThirdPartyNotification(parameterMap, "collectionType", "ownerId"))

        vertx.deployVerticle(FitbitVerticle(notificationService), testContext.succeeding {
            val client: WebClient = WebClient.create(vertx)
            client.post(8080, "localhost", "/fitbit/notification")
                    .putHeader("Content-Type","application/json")
                    .rxSendBuffer(Buffer.buffer(validJsonString))
                    .subscribe { response ->
                        testContext.verify {
                            GlobalScope.launch {
                                verify(notificationService).getUpdatedData(expectedNotificationList)
                                assertThat(response.statusCode()).isEqualTo(204)
                                testContext.completeNow()
                            }
                        }
                    }
        })
    }
}