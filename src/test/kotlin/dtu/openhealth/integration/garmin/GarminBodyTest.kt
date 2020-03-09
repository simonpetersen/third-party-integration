package dtu.openhealth.integration.garmin

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.HttpStatus

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class GarminBodyTest(@Autowired val testRestTemplate: TestRestTemplate) {

    val validJsonString ="""
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

    val invalidJsonString ="""
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
    fun testBodyEndpointValid() {
        val result = testRestTemplate.postForEntity("/api/garmin/body", validJsonString, String::class.java)
        assertThat(result.statusCode).isEqualTo(HttpStatus.CREATED)
    }

    @Test
    fun testBodyEndpointSyntaxError() {
        val result = testRestTemplate.postForEntity("/api/garmin/body", invalidJsonString, String::class.java)
        assertThat(result.statusCode).isEqualTo(HttpStatus.BAD_REQUEST)
        assertThat(result.body).isEqualTo("Json Syntax error")
    }


}
