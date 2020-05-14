package dtu.openhealth.integration.garmin.verticle

import com.nhaarman.mockitokotlin2.*
import dtu.openhealth.integration.garmin.GarminRouter
import dtu.openhealth.integration.garmin.data.SleepSummaryGarmin
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
class SleepEndpointTest {

    private val port = 8184
    private val validJsonString = """
    {
        "sleeps":
        [
            {
                "userId": "4aacafe82427c251df9c9592d0c06768", 
                "userAccessToken": "8f57a6f1-26ba-4b05-a7cd-c6b525a4c7a2", 
                "summaryId": "EXAMPLE_567890", 
                "calendarDate": "2016-01-10", 
                "durationInSeconds": 15264, 
                "startTimeInSeconds": 1452419581, 
                "startTimeOffsetInSeconds": 7200, 
                "unmeasurableSleepDurationInSeconds": 0, 
                "deepSleepDurationInSeconds": 11231, 
                "lightSleepDurationInSeconds": 3541, 
                "remSleepInSeconds": 0, 
                "awakeDurationInSeconds": 492, 
                "sleepLevelsMap": {
                    "deep": [ 
                        {
                            "startTimeInSeconds": 1452419581,
                            "endTimeInSeconds": 1452478724
                        }
                    ], 
                    "light": [
                        {
                            "startTimeInSeconds": 1452478725,
                            "endTimeInSeconds": 1452479725
                        }, 
                        {
                            "startTimeInSeconds": 1452481725,
                            "endTimeInSeconds": 1452484266
                        } 
                    ]
                },
                "validation": "DEVICE"
            },
            {
                "userId": "4aacafe82427c251df9c9592d0c06768", 
                "userAccessToken": "8f57a6f1-26ba-4b05-a7cd-c6b525a4c7a2",
                "summaryId": "EXAMPLE_567891", 
                "durationInSeconds": 11900, 
                "startTimeInSeconds": 1452467493, 
                "startTimeOffsetInSeconds": 7200, 
                "unmeasurableSleepDurationInSeconds": 0, 
                "deepSleepDurationInSeconds": 9446, 
                "lightSleepDurationInSeconds": 0, 
                "remSleepInSeconds": 2142, 
                "awakeDurationInSeconds": 312, 
                "sleepLevelsMap": {
                    "deep": [ 
                        {
                            "startTimeInSeconds": 1452467493,
                            "endTimeInSeconds": 1452476939
                        }
                    ], 
                    "rem": [
                        {
                            "startTimeInSeconds": 1452476940,
                            "endTimeInSeconds": 1452479082
                        } 
                    ]
                },
                "validation": "DEVICE",
                "timeOffsetSleepRespiration": {
                    "60": 15.31, 
                    "120": 14.58, 
                    "180": 12.73, 
                    "240": 12.87
                },
                "timeOffsetSleepSpo2": {
                    "0": 95,
                    "60": 96,
                    "120": 97,
                    "180": 93,
                    "240": 94,
                    "300": 95,
                    "360": 96
                }
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
            client.post(port, "localhost", "/sleep")
                    .putHeader("Content-Type", "application/json")
                    .rxSendBuffer(Buffer.buffer(validJsonString))
                    .subscribe(
                            { response ->
                                testContext.verify {
                                    assertThat(response.statusCode()).isEqualTo(200)
                                    verify(thirdPartyPushService, times(2))
                                            .saveDataToOMH(any<SleepSummaryGarmin>())
                                    testContext.completeNow()
                                }
                            },
                            { error -> testContext.failNow(error)}
                    )
        })

    }
}
