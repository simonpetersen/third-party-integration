package dtu.openhealth.integration.shared.service.pull

import dtu.openhealth.integration.kafka.producer.IKafkaProducerService
import dtu.openhealth.integration.shared.model.RestEndpoint
import dtu.openhealth.integration.shared.model.UserToken
import dtu.openhealth.integration.shared.service.ARetrievingService
import dtu.openhealth.integration.shared.service.http.IHttpService
import dtu.openhealth.integration.shared.service.token.refresh.ITokenRefreshService

abstract class AThirdPartyPullService(
        httpService: IHttpService,
        kafkaProducerService: IKafkaProducerService,
        private val endpointList: List<RestEndpoint>,
        private val tokenRefreshService: ITokenRefreshService
): ARetrievingService(httpService, kafkaProducerService) {

    suspend fun pullData()
    {
        val users = getUserList()
        users.forEach { callEndpointsForUser(it) }
    }

    abstract suspend fun getUserList(): List<UserToken>

    abstract fun prepareUserParameterList(userToken: UserToken): List<Map<String, String>>

    private fun callEndpointsForUser(userToken: UserToken)
    {
        if (tokenIsExpired(userToken.expireDateTime)) {
            tokenRefreshService.refreshToken(userToken) {
                updatedToken -> prepareParametersAndCallApi(updatedToken)
            }
        }
        else {
            prepareParametersAndCallApi(userToken)
        }
    }

    private fun prepareParametersAndCallApi(userToken: UserToken)
    {
        val parameterList = prepareUserParameterList(userToken)
        parameterList.forEach { callApiAndPublishOmhData(userToken, endpointList, it)}
    }
}