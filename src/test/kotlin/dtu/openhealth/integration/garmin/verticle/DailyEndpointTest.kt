package dtu.openhealth.integration.garmin.verticle

import dtu.openhealth.integration.garmin.GarminVerticle
import dtu.openhealth.integration.shared.service.mock.MockKafkaProducerService
import io.vertx.junit5.VertxExtension
import io.vertx.junit5.VertxTestContext
import io.vertx.reactivex.core.Vertx
import io.vertx.reactivex.core.buffer.Buffer
import io.vertx.reactivex.ext.web.client.WebClient
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.util.concurrent.TimeUnit

@ExtendWith(VertxExtension::class)
class DailyEndpointTest {

    val validJsonString ="""
    {
        "dailies":
        [
            {
                "userId": "4aacafe82427c251df9c9592d0c06768", 
                "userAccessToken": "8f57a6f1-26ba-4b05-a7cd-c6b525a4c7a2", 
                "summaryId": " EXAMPLE_67891", 
                "calendarDate": "2016-01-11", 
                "activityType": "WALKING", 
                "activeKilocalories": 321, 
                "bmrKilocalories": 1731, 
                "consumedCalories": 1121,
                "steps": 4210,
                "distanceInMeters": 3146.5, 
                "durationInSeconds": 86400, 
                "activeTimeInSeconds": 12240, 
                "startTimeInSeconds": 1452470400, 
                "startTimeOffsetInSeconds": 3600, 
                "moderateIntensityDurationInSeconds": 81870, 
                "vigorousIntensityDurationInSeconds": 4530, 
                "floorsClimbed": 8, 
                "minHeartRateInBeatsPerMinute": 59, 
                "averageHeartRateInBeatsPerMinute": 64, 
                "maxHeartRateInBeatsPerMinute": 112, 
                "timeOffsetHeartRateSamples": {
                    "15": 75,
                    "30": 75,
                    "3180": 76,
                    "3195": 65,
                    "3210": 65,
                    "3225": 73,
                    "3240": 74,
                    "3255": 74
                },
                "averageStressLevel": 43,
                "maxStressLevel": 87,
                "stressDurationInSeconds": 13620,
                "restStressDurationInSeconds": 7600,
                "activityStressDurationInSeconds": 3450, 
                "lowStressDurationInSeconds": 6700, 
                "mediumStressDurationInSeconds": 4350, 
                "highStressDurationInSeconds": 108000, 
                "stressQualifier": "stressful_awake", 
                "stepsGoal": 4500, 
                "netKilocaloriesGoal": 2010, 
                "intensityDurationGoalInSeconds": 1500, 
                "floorsClimbedGoal": 18
            }
        ]
    }"""

    @Test
    fun testValidRequestBody(vertx: Vertx, testContext: VertxTestContext) {
        vertx.deployVerticle(GarminVerticle(MockKafkaProducerService()), testContext.succeeding {
            val client: WebClient = WebClient.create(vertx)
            client.post(8184, "localhost", "/api/garmin/dailies")
                    .putHeader("Content-Type","application/json")
                    .rxSendBuffer(Buffer.buffer(validJsonString))
                    .subscribe { response ->
                        testContext.verify {
                            assertThat(response.statusCode()).isEqualTo(200)
                            testContext.completeNow()
                        }
                    }
        })
    }


}
