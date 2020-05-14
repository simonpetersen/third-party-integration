package dtu.openhealth.integration.garmin.verticle

import com.nhaarman.mockitokotlin2.*
import dtu.openhealth.integration.garmin.GarminRouter
import dtu.openhealth.integration.garmin.data.ActivitySummaryGarmin
import dtu.openhealth.integration.shared.service.ThirdPartyPushService
import dtu.openhealth.integration.shared.web.auth.IAuthorizationRouter
import io.vertx.junit5.VertxExtension
import io.vertx.junit5.VertxTestContext
import io.vertx.reactivex.core.Vertx
import io.vertx.reactivex.core.buffer.Buffer
import io.vertx.reactivex.ext.web.Router
import io.vertx.reactivex.ext.web.client.WebClient
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(VertxExtension::class)
class ActivityEndpointTest {

    private val port = 8184
    private val validJsonString ="""
    {
        "activities":
        [
            {
                "userId": "4aacafe82427c251df9c9592d0c06768", 
                "userAccessToken": "8f57a6f1-26ba-4b05-a7cd-c6b525a4c7a2", 
                "summaryId": "EXAMPLE_12345",
                "activityType": "RUNNING",
                "startTimeInSeconds": 1452470400, 
                "startTimeOffsetInSeconds": 0,
                "durationInSeconds": 11580, 
                "averageSpeedInMetersPerSecond": 2.888999938964844, 
                "distanceInMeters": 519818.125, 
                "activeKilocalories": 448,
                "deviceName": "Forerunner 910XT",
                "averagePaceInMinutesPerKilometer": 0.5975272352046997 
            },
            {
                "userId": "4aacafe82427c251df9c9592d0c06768", 
                "userAccessToken": "8f57a6f1-26ba-4b05-a7cd-c6b525a4c7a2", 
                "summaryId": "EXAMPLE_12346", 
                "activityType": "CYCLING", 
                "startTimeInSeconds": 1452506094, 
                "startTimeOffsetInSeconds": 0, 
                "durationInSeconds": 1824, 
                "averageSpeedInMetersPerSecond": 8.75, 
                "distanceInMeters": 4322.357, 
                "activeKilocalories": 360, 
                "deviceName": "Forerunner 910XT"
            } 
        ]
    }"""


    @Test
    fun testValidRequestBody(vertx: Vertx, testContext: VertxTestContext) {
        val thirdPartyPushService : ThirdPartyPushService = mock()
        val authRouter : IAuthorizationRouter = mock()
        whenever(authRouter.getRouter()).thenReturn(Router.router(vertx))
        val garminRouter = GarminRouter(vertx, thirdPartyPushService, authRouter)
        vertx.createHttpServer().requestHandler(garminRouter.getRouter()).listen(port, testContext.succeeding {
            val client: WebClient = WebClient.create(vertx)
            client.post(port, "localhost", "/activities")
                    .putHeader("Content-Type","application/json")
                    .rxSendBuffer(Buffer.buffer(validJsonString))
                    .subscribe(
                            { response ->
                                testContext.verify {
                                    assertThat(response.statusCode()).isEqualTo(200)
                                    verify(thirdPartyPushService, times(2))
                                            .saveDataToOMH(any<ActivitySummaryGarmin>())
                                    testContext.completeNow()
                                }
                            },
                            { error -> testContext.failNow(error)}
                    )
        })
    }

}
