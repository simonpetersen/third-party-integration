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
import io.vertx.reactivex.ext.web.client.WebClient
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(VertxExtension::class)
class FitbitVerticleTest {

    private val port = 8181
    private val correctVerificationCode = "bwgegGkBQhjsv4c7g"
    private val notificationJson ="""
    [{
	    "collectionType": "activities",
	    "date": "2020-03-05",
	    "ownerId": "User123",
	    "ownerType": "user",
	    "subscriptionId": "2345"
    }]"""

    @Test
    fun testWebhookCorrectVerificationCode(vertx: Vertx, testContext: VertxTestContext)
    {
        val expectedStatusCode = 204
        val notificationService : ThirdPartyNotificationService = mock()
        val authRouter : AuthorizationRouter = mock()
        whenever(authRouter.getRouter()).thenReturn(Router.router(vertx))
        val fitbitRouter = FitbitVerticle(vertx, notificationService, authRouter, correctVerificationCode)
        vertx.createHttpServer().requestHandler(fitbitRouter.getRouter()).listen(port, testContext.succeeding {
            verificationTestFunction(vertx, testContext, correctVerificationCode, expectedStatusCode)
        })
    }

    @Test
    fun testWebhookIncorrectVerificationCode(vertx: Vertx, testContext: VertxTestContext)
    {
        val expectedStatusCode = 404
        val incorrectVerificationCode = "abcd1234"
        val notificationService : ThirdPartyNotificationService = mock()
        val authRouter : AuthorizationRouter = mock()
        whenever(authRouter.getRouter()).thenReturn(Router.router(vertx))
        val fitbitRouter = FitbitVerticle(vertx, notificationService, authRouter, correctVerificationCode)
        vertx.createHttpServer().requestHandler(fitbitRouter.getRouter()).listen(port, testContext.succeeding {
            verificationTestFunction(vertx, testContext, incorrectVerificationCode, expectedStatusCode)
        })
    }

    @Test
    fun testNotificationEndpoint(vertx: Vertx, testContext: VertxTestContext)
    {
        val notificationService : ThirdPartyNotificationService = mock()
        val authRouter : AuthorizationRouter = mock()
        whenever(authRouter.getRouter()).thenReturn(Router.router(vertx))
        val fitbitRouter = FitbitVerticle(vertx, notificationService, authRouter, correctVerificationCode)

        vertx.createHttpServer().requestHandler(fitbitRouter.getRouter()).listen(port, testContext.succeeding {
            notificationTestFunction(vertx, testContext, notificationService)
        })
    }

    private fun verificationTestFunction(vertx: Vertx, testContext: VertxTestContext,
                                         verificationCode: String, expectedStatusCode: Int)
    {
        val client: WebClient = WebClient.create(vertx)
        client.get(port, "localhost", "/notification/webhook?verify=$verificationCode")
                .rxSend()
                .subscribe(
                        { response ->
                            testContext.verify {
                                assertThat(response.statusCode()).isEqualTo(expectedStatusCode)
                            }
                            testContext.completeNow()
                        },
                        { error ->
                            testContext.failNow(error)
                        })
    }

    private fun notificationTestFunction(vertx: Vertx, testContext: VertxTestContext, notificationService: ThirdPartyNotificationService)
    {
        val expectedNotificationList = getNotificationList()
        val client: WebClient = WebClient.create(vertx)
        client.post(port, "localhost", "/notification")
                .putHeader("Content-Type","application/json")
                .rxSendBuffer(Buffer.buffer(notificationJson))
                .subscribe(
                        { response ->
                            testContext.verify {
                                GlobalScope.launch {
                                    assertThat(response.statusCode()).isEqualTo(204)
                                    verify(notificationService).getUpdatedData(expectedNotificationList)
                                    testContext.completeNow()
                                }
                            }
                        },
                        { error -> testContext.failNow(error) }
                )
    }

    private fun getNotificationList() : List<ThirdPartyNotification>
    {
        val parameterMap = mapOf(Pair("collectionType", "activities"),
                Pair("date", "2020-03-05"),
                Pair("ownerId", "User123"),
                Pair("ownerType", "user"),
                Pair("subscriptionId", "2345"))
        return listOf(ThirdPartyNotification(parameterMap, "collectionType", "ownerId"))
    }
}
