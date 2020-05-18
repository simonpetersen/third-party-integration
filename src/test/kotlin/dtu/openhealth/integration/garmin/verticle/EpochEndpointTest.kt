package dtu.openhealth.integration.garmin.verticle

import com.nhaarman.mockitokotlin2.*
import dtu.openhealth.integration.garmin.GarminRouter
import dtu.openhealth.integration.garmin.data.EpochSummaryGarmin
import dtu.openhealth.integration.shared.service.push.IThirdPartyPushService
import dtu.openhealth.integration.shared.web.auth.IAuthorizationRouter
import io.vertx.junit5.VertxExtension
import io.vertx.junit5.VertxTestContext
import io.vertx.reactivex.core.Vertx
import io.vertx.reactivex.core.buffer.Buffer
import io.vertx.reactivex.ext.web.Router
import io.vertx.reactivex.ext.web.client.WebClient
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(VertxExtension::class)
class EpochEndpointTest {

    private val port = 8184
    private val validJsonString = """
    {
        "epochs":
        [
            {
                "userId": "4aacafe82427c251df9c9592d0c06768", 
                "userAccessToken": "8f57a6f1-26ba-4b05-a7cd-c6b525a4c7a2", 
                "summaryId": "EXAMPLE_1234", 
                "activityType": "SEDENTARY", 
                "activeKilocalories": 0, 
                "steps": 0, 
                "distanceInMeters": 0.0, 
                "durationInSeconds": 900, 
                "activeTimeInSeconds": 600, 
                "met": 1.0,
                "intensity": "SEDENTARY",
                "startTimeInSeconds": 1454418900,
                "startTimeOffsetInSeconds": 3600
            },
            {
                "userId": "4aacafe82427c251df9c9592d0c06768", 
                "userAccessToken": "8f57a6f1-26ba-4b05-a7cd-c6b525a4c7a2", 
                "summaryId": "EXAMPLE_5678", 
                "activityType": "RUNNING", 
                "activeKilocalories": 257, 
                "steps": 427, 
                "distanceInMeters": 222.07, 
                "durationInSeconds": 900, 
                "activeTimeInSeconds": 300, 
                "met": 9.894117,
                "intensity": "HIGHLY_ACTIVE", 
                "startTimeInSeconds": 1454418900, 
                "startTimeOffsetInSeconds": 3600
            }
        ]
    }"""


    @Test
    fun testValidRequestBody(vertx: Vertx, testContext: VertxTestContext) {
        val thirdPartyPushService : IThirdPartyPushService = mock()
        val authRouter : IAuthorizationRouter = mock()
        whenever(authRouter.getRouter()).thenReturn(Router.router(vertx))

        val garminRouter = GarminRouter(vertx, thirdPartyPushService, authRouter)
        vertx.createHttpServer().requestHandler(garminRouter.getRouter()).listen(port, testContext.succeeding {
            val client: WebClient = WebClient.create(vertx)
            client.post(port, "localhost", "/epochs")
                    .putHeader("Content-Type", "application/json")
                    .rxSendBuffer(Buffer.buffer(validJsonString))
                    .subscribe(
                            { response ->
                                testContext.verify {
                                    Assertions.assertThat(response.statusCode()).isEqualTo(200)
                                    verify(thirdPartyPushService, times(2))
                                            .saveDataToOMH(any<EpochSummaryGarmin>())
                                    testContext.completeNow()
                                }
                            },
                            { error -> testContext.failNow(error)}
                    )
        })
    }
}
