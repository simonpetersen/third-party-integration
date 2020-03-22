package dtu.openhealth.integration.service

import com.nhaarman.mockitokotlin2.*
import dtu.openhealth.integration.data.ThirdPartyData
import dtu.openhealth.integration.model.RestEndpoint
import dtu.openhealth.integration.model.User
import dtu.openhealth.integration.service.impl.HttpServiceImpl
import dtu.openhealth.integration.service.mock.MockRestUrl
import dtu.openhealth.integration.web.ApiResponse
import dtu.openhealth.integration.web.HttpConnectorClient
import io.reactivex.Single
import org.junit.jupiter.api.Test

class HttpServiceImplTest {

    @Test
    fun testCallApiForUser() {
        val testUrl1 = "/data/activities"
        val testUrl2 = "/data/sleep"
        val endpoint1 = RestEndpoint(MockRestUrl(testUrl1), ThirdPartyData.serializer())
        val endpoint2 = RestEndpoint(MockRestUrl(testUrl2), ThirdPartyData.serializer())
        val user = User("testUser", "123", "testToken")

        // Mock
        val httpClient: HttpConnectorClient = mock()
        val endpoints = listOf(endpoint1, endpoint2)
        val response = ApiResponse("{}", ThirdPartyData.serializer())
        whenever(httpClient.get(any(), any(), any())).thenReturn(Single.just(response))
        val httpService = HttpServiceImpl(httpClient, endpoints)

        httpService.callApiForUser(user, mapOf())

        // Verify expected calls to API
        verify(httpClient, times(endpoints.size)).get(any(), any(), any())
        verify(httpClient).get(eq(endpoint1), eq(testUrl1), eq(user.token))
        verify(httpClient).get(eq(endpoint2), eq(testUrl2), eq(user.token))
    }

    @Test
    fun testCallApiForUserWithParameters() {
        val testUrl1 = "/data/[userId]/activities"
        val testUrl2 = "/data/[userId]/sleep"
        val endpoint1 = RestEndpoint(MockRestUrl(testUrl1), ThirdPartyData.serializer())
        val endpoint2 = RestEndpoint(MockRestUrl(testUrl2), ThirdPartyData.serializer())
        val user = User("testUser", "123", "testToken")

        // Mock
        val httpClient: HttpConnectorClient = mock()
        val endpoints = listOf(endpoint1, endpoint2)
        val response = ApiResponse("{}", ThirdPartyData.serializer())
        whenever(httpClient.get(any(), any(), any())).thenReturn(Single.just(response))
        val httpService = HttpServiceImpl(httpClient, endpoints)

        httpService.callApiForUser(user, mapOf(Pair("userId", user.userId)))

        // Verify expected calls to API
        val expectedUrl1 = "/data/testUser/activities"
        val expectedUrl2 = "/data/testUser/sleep"
        verify(httpClient, times(endpoints.size)).get(any(), any(), any())
        verify(httpClient).get(eq(endpoint1), eq(expectedUrl1), eq(user.token))
        verify(httpClient).get(eq(endpoint2), eq(expectedUrl2), eq(user.token))
    }
}