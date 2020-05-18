package dtu.openhealth.integration.shared.service.notification

import com.nhaarman.mockitokotlin2.*
import dtu.openhealth.integration.kafka.producer.IKafkaProducerService
import dtu.openhealth.integration.shared.model.ThirdPartyData
import dtu.openhealth.integration.shared.model.RestEndpoint
import dtu.openhealth.integration.shared.model.ThirdPartyNotification
import dtu.openhealth.integration.shared.model.UserToken
import dtu.openhealth.integration.shared.service.data.usertoken.IUserTokenDataService
import dtu.openhealth.integration.shared.service.http.IHttpService
import dtu.openhealth.integration.shared.service.mock.MockRestUrl
import dtu.openhealth.integration.shared.service.tokenrefresh.ITokenRefreshService
import io.reactivex.Single
import kotlinx.coroutines.test.runBlockingTest
import org.junit.jupiter.api.Test
import java.time.LocalDateTime

class ThirdPartyNotificationServiceTest {

    private val userId = "testUser"
    private val extUserId = "dhsflkfs2"
    private val dataType = "data"
    private val accessToken = "token123"
    private val thirdParty = "thirdParty"

    @Test
    fun testNotification() = runBlockingTest {
        val tokenExpireDateTime = LocalDateTime.now().plusHours(2)
        val tokenRefreshInvocation = 0
        runTest(tokenRefreshInvocation, tokenExpireDateTime)
    }

    @Test
    fun testNotificationWithExpiredToken() = runBlockingTest {
        val tokenExpireDateTime = LocalDateTime.now().minusHours(1)
        val tokenRefreshInvocation = 1
        runTest(tokenRefreshInvocation, tokenExpireDateTime)
    }

    private suspend fun runTest(tokenRefreshInvocation: Int, tokenExpireDateTime: LocalDateTime) {
        val endpointMap = getEndpointMap()
        val parameters = mapOf(Pair("dataType", dataType), Pair("userId", extUserId))
        val notification = ThirdPartyNotification(parameters, "dataType", "userId")
        val user = UserToken(userId, extUserId, thirdParty, accessToken, expireDateTime = tokenExpireDateTime)

        // Mock
        val httpService: IHttpService = mock()
        val userService: IUserTokenDataService = mock()
        val kafkaProducerService: IKafkaProducerService = mock()
        val tokenRefreshService: ITokenRefreshService = mock()
        val expireDateTime = LocalDateTime.now().plusHours(8)
        val refreshedUser = UserToken(userId, extUserId, thirdParty, accessToken, expireDateTime = expireDateTime)
        whenever(tokenRefreshService.refreshToken(user)).thenReturn(refreshedUser)
        whenever(userService.getUserByExtId(extUserId)).thenReturn(user)
        whenever(httpService.callApiForUser(any(), any(), any())).thenReturn(Single.just(emptyList()))

        // Call notificationService
        val notificationService = ThirdPartyNotificationServiceImpl(httpService, endpointMap, userService, kafkaProducerService, tokenRefreshService)
        notificationService.getUpdatedData(listOf(notification))

        // Verify
        val expectedEndpointList = endpointMap[dataType] ?: emptyList()
        val expectedUser = if (tokenRefreshInvocation == 0) user else refreshedUser
        verify(httpService).callApiForUser(eq(expectedEndpointList), eq(expectedUser), eq(parameters))
        verify(tokenRefreshService, times(tokenRefreshInvocation)).refreshToken(any())
    }


    private fun getEndpointMap() : Map<String, List<RestEndpoint>> {
        val testUrl1 = "/data/activities"
        val testUrl2 = "/data/sleep"
        val endpoint1 = RestEndpoint(MockRestUrl(testUrl1), ThirdPartyData.serializer())
        val endpoint2 = RestEndpoint(MockRestUrl(testUrl2), ThirdPartyData.serializer())
        val endpointList = listOf(endpoint1, endpoint2)
        return mapOf(Pair(dataType, endpointList))
    }
}