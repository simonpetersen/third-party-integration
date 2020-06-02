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
import dtu.openhealth.integration.shared.service.token.refresh.ITokenRefreshService
import io.reactivex.Single
import kotlinx.coroutines.test.runBlockingTest
import org.junit.jupiter.api.Test
import java.time.LocalDateTime

class ThirdPartyNotificationServiceTest {

    private val userId = "testUser"
    private val extUserId = "dhsflkfs2"
    private val activityDataType = "activity"
    private val sleepDataType = "sleep"
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
        val activityParameters = mapOf(Pair("dataType", activityDataType), Pair("userId", extUserId))
        val sleepParameters = mapOf(Pair("dataType", sleepDataType), Pair("userId", extUserId))
        val activityNotification = ThirdPartyNotification(activityParameters, "dataType", "userId")
        val sleepNotification = ThirdPartyNotification(sleepParameters, "dataType", "userId")
        val userToken = UserToken(userId, extUserId, thirdParty, accessToken, expireDateTime = tokenExpireDateTime)

        // Mock
        val httpService: IHttpService = mock()
        val userService: IUserTokenDataService = mock()
        val kafkaProducerService: IKafkaProducerService = mock()
        val tokenRefreshService: ITokenRefreshService = mock()
        whenever(userService.getUserByExtId(extUserId)).thenReturn(userToken)
        whenever(httpService.callApiForUser(any(), any(), any())).thenReturn(Single.just(emptyList()))

        // Call notificationService
        val notificationService = ThirdPartyNotificationServiceImpl(httpService, endpointMap, userService, kafkaProducerService, tokenRefreshService)
        notificationService.getUpdatedData(listOf(activityNotification, sleepNotification))

        // Verify tokenRefresh
        verify(tokenRefreshService, times(tokenRefreshInvocation)).refreshToken(any(), any())

        if (tokenRefreshInvocation == 0)
        {
            // Verify activity call
            val activityEndpointList = endpointMap[activityDataType] ?: emptyList()
            verify(httpService).callApiForUser(eq(activityEndpointList), eq(userToken), eq(activityParameters))

            // Verify sleep call
            val sleepEndpointList = endpointMap[sleepDataType] ?: emptyList()
            verify(httpService).callApiForUser(eq(sleepEndpointList), eq(userToken), eq(sleepParameters))
        }
    }


    private fun getEndpointMap() : Map<String, List<RestEndpoint>> {
        val activityTestUrl = "/data/activities"
        val sleepTestUrl = "/data/sleep"
        val activityEndpoint = RestEndpoint(MockRestUrl(activityTestUrl), ThirdPartyData.serializer())
        val sleepEndpoint = RestEndpoint(MockRestUrl(sleepTestUrl), ThirdPartyData.serializer())
        return mapOf(Pair(activityDataType, listOf(activityEndpoint)), Pair(sleepDataType, listOf(sleepEndpoint)))
    }
}