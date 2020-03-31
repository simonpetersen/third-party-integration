package dtu.openhealth.integration.service

import com.nhaarman.mockitokotlin2.*
import dtu.openhealth.integration.mapping.ThirdPartyMapper
import dtu.openhealth.integration.model.RestEndpoint
import dtu.openhealth.integration.model.User
import dtu.openhealth.integration.service.mock.MockPullService
import io.reactivex.Single
import org.junit.jupiter.api.Test

class ThirdPartyPullServiceTest {

    @Test
    fun testDataPulling_SingleUser() {
        val users = listOf(User("testUser", "123", "123"))
        val httpService: HttpService = mock()
        val endpointList = emptyList<RestEndpoint>()
        whenever(httpService.callApiForUser(any(), any(), any()))
                .thenReturn(Single.just(emptyList()))

        // Call pulling service.
        val pullingService = MockPullService(httpService, endpointList, users)
        pullingService.pullData()

        // Verify expected call to httpService.
        val user = User("testUser", "123", "123")
        verify(httpService).callApiForUser(any(), eq(user), eq(HashMap()))
    }

    @Test
    fun testDataPulling_MultipleUsers() {
        val users = listOf(User("testUser", "123", "123"),
                User("testUser2", "abc", "abc"))
        val httpService: HttpService = mock()
        val endpointList = emptyList<RestEndpoint>()
        whenever(httpService.callApiForUser(any(), any(), any()))
                .thenReturn(Single.just(emptyList()))

        // Call pulling service.
        val pullingService = MockPullService(httpService, endpointList, users)
        pullingService.pullData()

        // Verify expected calls to httpService.
        users.forEach { verify(httpService).callApiForUser(any(), eq(it), eq(mapOf())) }
    }
}