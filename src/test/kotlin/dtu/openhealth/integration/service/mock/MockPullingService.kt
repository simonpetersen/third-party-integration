package dtu.openhealth.integration.service.mock

import dtu.openhealth.integration.mapping.ThirdPartyMapper
import dtu.openhealth.integration.model.User
import dtu.openhealth.integration.service.HttpService
import dtu.openhealth.integration.service.ThirdPartyPullingService

class MockPullingService(
        mapper: ThirdPartyMapper,
        httpService: HttpService,
        private val users: List<User>
): ThirdPartyPullingService(mapper, httpService) {

    override fun getUserList(): List<User> {
        return users
    }

    override fun getUserParameters(user: User): Map<String, String> {
        return mapOf()
    }
}