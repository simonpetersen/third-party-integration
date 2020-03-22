package dtu.openhealth.integration.service.mock

import dtu.openhealth.integration.mapping.ThirdPartyMapper
import dtu.openhealth.integration.model.User
import dtu.openhealth.integration.service.HttpService
import dtu.openhealth.integration.service.ThirdPartyPullService

class MockPullService(
        httpService: HttpService,
        private val users: List<User>
): ThirdPartyPullService(httpService) {

    override fun getUserList(): List<User> {
        return users
    }

    override fun getUserParameters(user: User): Map<String, String> {
        return mapOf()
    }
}