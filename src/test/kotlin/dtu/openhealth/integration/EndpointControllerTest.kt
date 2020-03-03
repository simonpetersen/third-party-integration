package dtu.openhealth.integration

import dtu.openhealth.integration.request.FitbitNotification
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.test.web.client.postForEntity
import org.springframework.http.HttpEntity
import org.springframework.http.HttpStatus

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class EndpointControllerTest(@Autowired val restTemplate : TestRestTemplate) {

    @Test
    fun testNotification() {
        val request = HttpEntity(FitbitNotification("user", "123", "activities", "2020-02-03"))
        val entity = restTemplate.postForEntity<String>("/notification", request)
        assertThat(entity.statusCode).isEqualTo(HttpStatus.NO_CONTENT)
        assertThat(entity.body).isNull()
    }
}