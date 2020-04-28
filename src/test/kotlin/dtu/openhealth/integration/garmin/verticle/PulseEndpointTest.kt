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

@ExtendWith(VertxExtension::class)
class PulseEndpointTest {

    private val validJsonString = """
    {
        "pulseOX":
        [
            {
                "userId": "4aacafe82427c251df9c9592d0c06768", 
                "userAccessToken": "8f57a6f1-26ba-4b05-a7cd-c6b525a4c7a2", 
                "summaryId": " EXAMPLE_34343", 
                "calendarDate": "2018-08-27", 
                "startTimeInSeconds": 1535400706, 
                "durationInSeconds": 86400, 
                "startTimeOffsetInSeconds": 7200, 
                "timeOffsetSpo2Values": {
                    "0": 98,
                    "3600": 95,
                    "7200": 93,
                    "10800": 98
                }
            }
        ]
    }"""


    @Test
    fun testValidRequestBody(vertx: Vertx, testContext: VertxTestContext) {
        vertx.deployVerticle(GarminVerticle(MockKafkaProducerService()), testContext.succeeding {
            val client: WebClient = WebClient.create(vertx)
            client.post(8084, "localhost", "/api/garmin/pulse")
                    .putHeader("Content-Type", "application/json")
                    .rxSendBuffer(Buffer.buffer(validJsonString))
                    .subscribe { response ->
                        testContext.verify {
                            assertThat(response.statusCode()).isEqualTo(200)
                        }
                        testContext.completeNow()
                    }
        })
    }

}
