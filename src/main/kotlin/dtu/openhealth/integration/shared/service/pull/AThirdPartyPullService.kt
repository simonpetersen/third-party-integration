package dtu.openhealth.integration.shared.service.pull

import dtu.openhealth.integration.kafka.producer.IKafkaProducerService
import dtu.openhealth.integration.shared.model.RestEndpoint
import dtu.openhealth.integration.shared.model.ThirdPartyData
import dtu.openhealth.integration.shared.model.UserToken
import dtu.openhealth.integration.shared.service.ARetrievingService
import dtu.openhealth.integration.shared.service.http.IHttpService
import dtu.openhealth.integration.shared.service.tokenrefresh.ITokenRefreshService
import io.vertx.core.logging.LoggerFactory
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import java.time.LocalDateTime

abstract class AThirdPartyPullService(
        httpService: IHttpService,
        kafkaProducerService: IKafkaProducerService,
        private val endpointList: List<RestEndpoint>,
        private val tokenRefreshService: ITokenRefreshService
): ARetrievingService(httpService, kafkaProducerService) {

    suspend fun pullData() {
        val users = getUserList()
        users.forEach { callEndpointsForUser(it) }
    }

    abstract suspend fun getUserList(): List<UserToken>

    abstract fun getUserParameterList(userToken: UserToken): List<Map<String, String>>

    private suspend fun callEndpointsForUser(userToken: UserToken)
    {
        if (tokenIsExpired(userToken.expireDateTime)) {
            val updatedUserToken = tokenRefreshService.refreshToken(userToken)
            prepareParametersAndCallApi(updatedUserToken)
        }
        else {
            prepareParametersAndCallApi(userToken)
        }
    }

    private fun prepareParametersAndCallApi(userToken: UserToken)
    {
        val parameterList = getUserParameterList(userToken)
        parameterList.forEach { callApiAndPublishOmhData(userToken, endpointList, it)}
    }
}