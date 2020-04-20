package dtu.openhealth.integration.shared.service

import com.nhaarman.mockitokotlin2.*
import dtu.openhealth.integration.shared.data.ThirdPartyData
import dtu.openhealth.integration.shared.model.RestEndpoint
import dtu.openhealth.integration.shared.model.ThirdPartyNotification
import dtu.openhealth.integration.shared.model.User
import dtu.openhealth.integration.shared.service.impl.ThirdPartyNotificationServiceImpl
import dtu.openhealth.integration.shared.service.mock.MockRestUrl
import io.reactivex.Single
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test

class ThirdPartyNotificationServiceTest {

    private val USER_ID = "testUser"
    private val DATA_TYPE = "data"

    @Test
    fun testNotification() = runBlocking {
        val testUrl1 = "/data/activities"
        val testUrl2 = "/data/sleep"
        val endpoint1 = RestEndpoint(MockRestUrl(testUrl1), ThirdPartyData.serializer())
        val endpoint2 = RestEndpoint(MockRestUrl(testUrl2), ThirdPartyData.serializer())
        val endpointList = listOf(endpoint1, endpoint2)
        val endpointMap = mapOf(Pair(DATA_TYPE, endpointList))
        val user = User(USER_ID, "123", "testToken")
        val parameters = mapOf(Pair("dataType", DATA_TYPE), Pair("userId", USER_ID))
        val notification = ThirdPartyNotification(parameters, "dataType", "userId")

        // Mock
        val httpService: HttpService = mock()
        val userService: UserDataService = mock()
        whenever(userService.getUserById(USER_ID)).thenReturn(user)
        whenever(httpService.callApiForUser(any(), any(), any())).thenReturn(Single.just(emptyList()))
        val notificationService = ThirdPartyNotificationServiceImpl(httpService, endpointMap, userService)

        notificationService.getUpdatedData(listOf(notification))

        verify(httpService).callApiForUser(eq(endpointList), eq(user), eq(parameters))
    }
}