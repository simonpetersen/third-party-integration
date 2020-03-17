package dtu.openhealth.integration.service

import com.nhaarman.mockitokotlin2.*
import dtu.openhealth.integration.data.ThirdPartyData
import dtu.openhealth.integration.model.RestEndpoint
import dtu.openhealth.integration.model.User
import dtu.openhealth.integration.service.impl.HttpServiceImpl
import dtu.openhealth.integration.web.HttpConnectorClient
import org.junit.jupiter.api.Test

class HttpServiceImplTest {

    @Test
    fun testCallApiForUser() {
        val testUrl1 = "https:://www.someapi.com/data/activities"
        val testUrl2 = "https:://www.someapi.com/data/sleep"
        val user = User("testUser", "123", "testToken")

        // Mock
        val httpClient: HttpConnectorClient = mock()
        val endpoints = listOf(RestEndpoint(testUrl1, ThirdPartyData.serializer()), RestEndpoint(testUrl2, ThirdPartyData.serializer()))
        whenever(httpClient.get(any(), any())).thenReturn("{}")
        val httpService = HttpServiceImpl(httpClient, endpoints)

        httpService.callApiForUser(user, mapOf())

        // Verify expected calls to API
        verify(httpClient).get(eq(testUrl1), eq(user.token))
        verify(httpClient).get(eq(testUrl2), eq(user.token))
    }

    @Test
    fun testCallApiForUserWithParameters() {
        val testUrl1 = "https:://www.someapi.com/data/[userId]/activities"
        val testUrl2 = "https:://www.someapi.com/data/[userId]/sleep"
        val user = User("testUser", "123", "testToken")

        // Mock
        val httpClient: HttpConnectorClient = mock()
        val endpoints = listOf(RestEndpoint(testUrl1, ThirdPartyData.serializer()), RestEndpoint(testUrl2, ThirdPartyData.serializer()))
        whenever(httpClient.get(any(), any())).thenReturn("{}")
        val httpService = HttpServiceImpl(httpClient, endpoints)

        httpService.callApiForUser(user, mapOf(Pair("userId", user.userId)))

        // Verify expected calls to API
        val expectedUrl1 = "https:://www.someapi.com/data/testUser/activities"
        val expectedUrl2 = "https:://www.someapi.com/data/testUser/sleep"
        verify(httpClient).get(eq(expectedUrl1), eq(user.token))
        verify(httpClient).get(eq(expectedUrl2), eq(user.token))
    }
}