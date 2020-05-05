package dtu.openhealth.integration.shared.service.mock

import dtu.openhealth.integration.shared.model.RestEndpoint
import dtu.openhealth.integration.shared.model.UserToken
import dtu.openhealth.integration.shared.service.HttpService
import dtu.openhealth.integration.shared.service.ThirdPartyPullService

class MockPullService(
        httpService: HttpService,
        endpointList: List<RestEndpoint>,
        private val userTokens: List<UserToken>
): ThirdPartyPullService(httpService, endpointList) {

    override fun getUserList(): List<UserToken> {
        return userTokens
    }

    override fun getUserParameters(userToken: UserToken): Map<String, String> {
        return mapOf()
    }
}