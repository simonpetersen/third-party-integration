package dtu.openhealth.integration.shared.service.mock

import dtu.openhealth.integration.kafka.producer.IKafkaProducerService
import dtu.openhealth.integration.shared.model.RestEndpoint
import dtu.openhealth.integration.shared.model.UserToken
import dtu.openhealth.integration.shared.service.http.IHttpService
import dtu.openhealth.integration.shared.service.pull.AThirdPartyPullService
import dtu.openhealth.integration.shared.service.token.refresh.ITokenRefreshService

class MockPullService(
        httpService: IHttpService,
        endpointList: List<RestEndpoint>,
        kafkaProducerService: IKafkaProducerService,
        tokenRefreshService: ITokenRefreshService,
        private val userTokens: List<UserToken>
): AThirdPartyPullService(httpService, kafkaProducerService, endpointList, tokenRefreshService) {

    override suspend fun getUserList(): List<UserToken> {
        return userTokens
    }

    override fun prepareUserParameterList(userToken: UserToken): List<Map<String, String>> {
        return listOf(emptyMap())
    }
}