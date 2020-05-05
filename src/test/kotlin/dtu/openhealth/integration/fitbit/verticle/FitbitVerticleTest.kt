package dtu.openhealth.integration.fitbit.verticle

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import dtu.openhealth.integration.fitbit.FitbitVerticle
import dtu.openhealth.integration.shared.model.ThirdPartyNotification
import dtu.openhealth.integration.shared.service.ThirdPartyNotificationService
import dtu.openhealth.integration.shared.web.auth.AuthorizationRouter
import io.vertx.junit5.VertxExtension
import io.vertx.junit5.VertxTestContext
import io.vertx.reactivex.core.Vertx
import io.vertx.reactivex.core.buffer.Buffer
import io.vertx.reactivex.ext.web.Router
import io.vertx.reactivex.ext.web.client.HttpResponse
import io.vertx.reactivex.ext.web.client.WebClient
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(VertxExtension::class)
class FitbitVerticleTest {

    private val port = 8181
    private val validJsonString ="""
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
        val authRouter : AuthorizationRouter = mock()
        whenever(authRouter.getRouter()).thenReturn(Router.router(vertx))

        vertx.deployVerticle(FitbitVerticle(notificationService, authRouter), testContext.succeeding {
            val client: WebClient = WebClient.create(vertx)
            client.post(port, "localhost", "/fitbit/notification")
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
