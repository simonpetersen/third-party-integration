package dtu.openhealth.integration.service.mock

import dtu.openhealth.integration.model.RestEndpoint
import dtu.openhealth.integration.model.User
import dtu.openhealth.integration.service.HttpService
import dtu.openhealth.integration.service.ThirdPartyPullService

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