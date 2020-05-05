package dtu.openhealth.integration.shared.service

import com.nhaarman.mockitokotlin2.*
import dtu.openhealth.integration.shared.model.ThirdPartyData
import dtu.openhealth.integration.shared.model.RestEndpoint
import dtu.openhealth.integration.shared.model.UserToken
import dtu.openhealth.integration.shared.service.impl.HttpServiceImpl
import dtu.openhealth.integration.shared.service.mock.MockRestUrl
import dtu.openhealth.integration.shared.web.ApiRequest
import dtu.openhealth.integration.shared.web.ApiResponse
import dtu.openhealth.integration.shared.web.HttpConnectorClient
import io.reactivex.Single
import org.junit.jupiter.api.Test

class HttpServiceImplTest {

    @Test
    fun testCallApiForUser() {
        val testUrl1 = "/data/activities"
        val testUrl2 = "/data/sleep"
        val endpoint1 = RestEndpoint(MockRestUrl(testUrl1), ThirdPartyData.serializer())
        val endpoint2 = RestEndpoint(MockRestUrl(testUrl2), ThirdPartyData.serializer())
        val user = UserToken("testUser", "123", "testToken")

        // Mock
        val httpClient: HttpConnectorClient = mock()
        val endpoints = listOf(endpoint1, endpoint2)
        val response = ApiResponse("{}", ThirdPartyData.serializer(), emptyMap())
        whenever(httpClient.get(any(), any())).thenReturn(Single.just(response))
        val httpService = HttpServiceImpl(httpClient)

        httpService.callApiForUser(endpoints, user, mapOf())

        // Verify expected calls to API
        val request1 = ApiRequest(endpoint1, testUrl1, emptyMap())
        val request2 = ApiRequest(endpoint2, testUrl2, emptyMap())
        verify(httpClient, times(endpoints.size)).get(any(), any())
        verify(httpClient).get(eq(request1), eq(user))
        verify(httpClient).get(eq(request2), eq(user))
    }

    @Test
    fun testCallApiForUserWithParameters() {
        val testUrl1 = "/data/[userId]/activities"
        val testUrl2 = "/data/[userId]/sleep"
        val endpoint1 = RestEndpoint(MockRestUrl(testUrl1), ThirdPartyData.serializer())
        val endpoint2 = RestEndpoint(MockRestUrl(testUrl2), ThirdPartyData.serializer())
        val user = UserToken("testUser", "123", "testToken")
        val parameters = mapOf(Pair("userId", user.userId))

        // Mock
        val httpClient: HttpConnectorClient = mock()
        val endpoints = listOf(endpoint1, endpoint2)
        val response = ApiResponse("{}", ThirdPartyData.serializer(), parameters)
        whenever(httpClient.get(any(), any())).thenReturn(Single.just(response))
        val httpService = HttpServiceImpl(httpClient)

        httpService.callApiForUser(endpoints, user, parameters)

        // Verify expected calls to API
        val expectedUrl1 = "/data/testUser/activities"
        val request1 = ApiRequest(endpoint1, expectedUrl1, parameters)
        val expectedUrl2 = "/data/testUser/sleep"
        val request2 = ApiRequest(endpoint2, expectedUrl2, parameters)
        verify(httpClient, times(endpoints.size)).get(any(), any())
        verify(httpClient).get(eq(request1), eq(user))
        verify(httpClient).get(eq(request2), eq(user))
    }
}