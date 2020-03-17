package dtu.openhealth.integration.service

import com.nhaarman.mockitokotlin2.*
import dtu.openhealth.integration.mapping.ThirdPartyMapper
import dtu.openhealth.integration.model.User
import dtu.openhealth.integration.service.impl.HttpServiceImpl
import dtu.openhealth.integration.service.mock.MockPullingService
import org.junit.jupiter.api.Test

class ThirdPartyPullingServiceTest {

    @Test
    fun testDataPulling_SingleUser() {
        val users = listOf(User("testUser", "123", "123"))
        val mapper: ThirdPartyMapper = mock()
        val httpService: HttpService = mock()

        // Call pulling service.
        val pullingService = MockPullingService(mapper, httpService, users)
        pullingService.pullData()

        // Verify expected call to httpService.
        verify(httpService).callApiForUser(eq(User("testUser", "123", "123")), eq(HashMap()))
    }

    @Test
    fun testDataPulling_MultipleUsers() {
        val users = listOf(User("testUser", "123", "123"),
                User("testUser2", "abc", "abc"))
        val mapper: ThirdPartyMapper = mock()
        val httpService: HttpService = mock()

        // Call pulling service.
        val pullingService = MockPullingService(mapper, httpService, users)
        pullingService.pullData()

        // Verify expected calls to httpService.
        users.forEach { verify(httpService).callApiForUser(eq(it), eq(mapOf())) }
    }
}