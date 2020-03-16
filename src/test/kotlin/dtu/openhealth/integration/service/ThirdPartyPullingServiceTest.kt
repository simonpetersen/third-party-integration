package dtu.openhealth.integration.service

import com.nhaarman.mockitokotlin2.*
import dtu.openhealth.integration.data.ThirdPartyData
import dtu.openhealth.integration.mapping.ThirdPartyMapper
import dtu.openhealth.integration.model.RestEndpoint
import dtu.openhealth.integration.service.mock.MockPullingService
import dtu.openhealth.integration.web.HttpConnector
import org.junit.jupiter.api.Test

class ThirdPartyPullingServiceTest {

    private val testUrl1: String = "https:://www.someapi.com/data/activities"
    private val testUrl2: String = "https:://www.someapi.com/data/sleep"

    @Test
    fun testDataPulling() {
        val mapper: ThirdPartyMapper = mock()
        val httpConnector: HttpConnector = mock()
        val endpoints = listOf(RestEndpoint(testUrl1, ThirdPartyData.serializer()), RestEndpoint(testUrl2, ThirdPartyData.serializer()))
        whenever(httpConnector.get(any(), any())).thenReturn("{}")

        // Call pulling service.
        val pullingService = MockPullingService(mapper, endpoints, httpConnector)
        pullingService.pullData()

        // Verify expected calls to API.
        verify(httpConnector).get(eq(testUrl1), any())
        verify(httpConnector).get(eq(testUrl2), any())
    }
}