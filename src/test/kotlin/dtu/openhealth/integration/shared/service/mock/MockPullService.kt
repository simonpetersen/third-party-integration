package dtu.openhealth.integration.shared.service.mock

import dtu.openhealth.integration.shared.model.RestEndpoint
import dtu.openhealth.integration.shared.model.User
import dtu.openhealth.integration.shared.service.HttpService
import dtu.openhealth.integration.shared.service.ThirdPartyPullService

class MockPullService(
        httpService: HttpService,
        endpointList: List<RestEndpoint>,
        private val users: List<User>
): ThirdPartyPullService(httpService, endpointList) {

    override fun getUserList(): List<User> {
        return users
    }

    override fun getUserParameters(user: User): Map<String, String> {
        return mapOf()
    }
}