package dtu.openhealth.integration

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.test.web.client.getForEntity
import org.springframework.http.HttpStatus.OK

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AuthorizationControllerTest(@Autowired val restTemplate : TestRestTemplate) {

    @Test
    fun testAuth() {
        val entity = restTemplate.getForEntity<String>("/auth?oauth_token=22&oauth_verifier=22")
        assertThat(entity.statusCode).isEqualTo(OK)
        assertThat(entity.body).isEqualTo("12345")
    }

    @Test
    fun testLogin() {
        val entity = restTemplate.getForEntity<String>("/login?code=123")
        assertThat(entity.statusCode).isEqualTo(OK)
        assertThat(entity.body).isEqualTo("Login successful")
    }
}