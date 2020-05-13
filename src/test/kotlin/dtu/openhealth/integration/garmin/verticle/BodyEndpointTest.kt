package dtu.openhealth.integration.garmin.verticle

import com.nhaarman.mockitokotlin2.*
import dtu.openhealth.integration.garmin.GarminRouter
import dtu.openhealth.integration.garmin.data.BodyCompositionSummaryGarmin
import dtu.openhealth.integration.shared.service.ThirdPartyPushService
import dtu.openhealth.integration.shared.web.auth.AuthorizationRouter
import io.vertx.reactivex.core.Vertx
import io.vertx.junit5.VertxExtension
import io.vertx.junit5.VertxTestContext
import io.vertx.reactivex.core.buffer.Buffer
import io.vertx.reactivex.ext.web.Router
import io.vertx.reactivex.ext.web.client.WebClient
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(VertxExtension::class)
class BodyEndpointTest {

    private val port = 8184
    private val validJsonString = """
    {
        "body":
        [
            {
                "userId": "4aacafe82427c251df9c9592d0c06768", 
                "userAccessToken": "8f57a6f1-26ba-4b05-a7cd-c6b525a4c7a2", 
                "summaryId": "EXAMPLE_678901", 
                "measurementTimeInSeconds": 1439741130, 
                "measurementTimeOffsetInSeconds": 0, 
                "muscleMassInGrams": 25478, 
                "boneMassInGrams": 2437, 
                "bodyWaterInPercent": 59.4, 
                "bodyFatInPercent": 17.1, 
                "bodyMassIndex": 23.2, 
                "weightInGrams": 75450
            }
            ]
    }"""

    private val invalidJsonString = """
    {
        "body":
        [
            {
                "userId": "4aacafe82427c251df9c9592d0c06768", 
                "userAccessToken": "8f57a6f1-26ba-4b05-a7cd-c6b525a4c7a2", 
                "summaryId": "EXAMPLE_678901", 
                "measurementTimeInSeconds": 1439741130, 
                "measurementTimeOffsetInSeconds": 0, 
                "muscleMassInGrams": 25478
                "boneMassInGrams": 2437
                "bodyWaterInPercent": 59.4, 
                "bodyFatInPercent": 17.1, 
                "bodyMassIndex": 23.2, 
                "weightInGrams": 75450
            }
            ]
    }"""


    @Test
    fun testValidRequestBody(vertx: Vertx, testContext: VertxTestContext) {
        val thirdPartyPushService : ThirdPartyPushService = mock()
        val authRouter : AuthorizationRouter = mock()
        whenever(authRouter.getRouter()).thenReturn(Router.router(vertx))

        val garminRouter = GarminRouter(vertx, thirdPartyPushService, authRouter)
        vertx.createHttpServer().requestHandler(garminRouter.getRouter()).listen(port, testContext.succeeding {
            val client: WebClient = WebClient.create(vertx)
            client.post(port, "localhost", "/body")
                    .putHeader("Content-Type","application/json")
                    .rxSendBuffer(Buffer.buffer(validJsonString))
                    .subscribe(
                            { response ->
                                testContext.verify {
                                    assertThat(response.statusCode()).isEqualTo(200)
                                    verify(thirdPartyPushService).saveDataToOMH(any<BodyCompositionSummaryGarmin>())
                                    testContext.completeNow()
                                }
                            },
                            { error -> testContext.failNow(error)}
                    )
        })
    }

    @Test
    fun testInvalidRequestBody(vertx: Vertx, testContext: VertxTestContext) {
        val thirdPartyPushService : ThirdPartyPushService = mock()
        val authRouter : AuthorizationRouter = mock()
        whenever(authRouter.getRouter()).thenReturn(Router.router(vertx))

        val garminRouter = GarminRouter(vertx, thirdPartyPushService, authRouter)
        vertx.createHttpServer().requestHandler(garminRouter.getRouter()).listen(port, testContext.succeeding {
            val client: WebClient = WebClient.create(vertx)
            client.post(port, "localhost", "/body")
                    .putHeader("Content-Type","application/json")
                    .rxSendBuffer(Buffer.buffer(invalidJsonString))
                    .subscribe(
                            { response ->
                                testContext.verify {
                                    assertThat(response.statusCode()).isEqualTo(500)
                                    verify(thirdPartyPushService, times(0))
                                            .saveDataToOMH(any<BodyCompositionSummaryGarmin>())
                                    testContext.completeNow()
                                }
                            },
                            { error -> testContext.failNow(error)}
                    )
        })
    }

}
