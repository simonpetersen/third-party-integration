package dtu.openhealth.integration.shared.service.pull

import com.nhaarman.mockitokotlin2.*
import dtu.openhealth.integration.kafka.producer.IKafkaProducerService
import dtu.openhealth.integration.shared.model.RestEndpoint
import dtu.openhealth.integration.shared.model.UserToken
import dtu.openhealth.integration.shared.service.http.IHttpService
import dtu.openhealth.integration.shared.service.mock.MockPullService
import dtu.openhealth.integration.shared.service.tokenrefresh.ITokenRefreshService
import io.reactivex.Single
import kotlinx.coroutines.test.runBlockingTest
import org.junit.jupiter.api.Test

class ThirdPartyPullServiceTest {

    @Test
    fun testDataPulling_SingleUser() = runBlockingTest {
        val userTokens = listOf(UserToken("testUser", "123","thirdParty","123"))
        val httpService: IHttpService = mock()
        val kafkaProducerService: IKafkaProducerService = mock()
        val tokenRefreshService: ITokenRefreshService = mock()
        val endpointList = emptyList<RestEndpoint>()
        whenever(httpService.callApiForUser(any(), any(), any()))
                .thenReturn(Single.just(emptyList()))

        // Call pulling service.
        val pullingService = MockPullService(httpService, endpointList, kafkaProducerService, tokenRefreshService, userTokens)
        pullingService.pullData()

        // Verify expected call to httpService.
        val user = UserToken("testUser", "123","thirdParty", "123")
        verify(httpService).callApiForUser(any(), eq(user), eq(HashMap()))
    }

    @Test
    fun testDataPulling_MultipleUsers() = runBlockingTest {
        val userTokens = listOf(UserToken("testUser", "123", "thirdParty","123"),
                UserToken("testUser2", "abc", "thirdParty","abc"))
        val httpService: IHttpService = mock()
        val kafkaProducerService: IKafkaProducerService = mock()
        val tokenRefreshService: ITokenRefreshService = mock()
        val endpointList = emptyList<RestEndpoint>()
        whenever(httpService.callApiForUser(any(), any(), any()))
                .thenReturn(Single.just(emptyList()))

        // Call pulling service.
        val pullingService = MockPullService(httpService, endpointList, kafkaProducerService, tokenRefreshService, userTokens)
        pullingService.pullData()

        // Verify expected calls to httpService.
        userTokens.forEach { verify(httpService).callApiForUser(any(), eq(it), eq(mapOf())) }
    }
}