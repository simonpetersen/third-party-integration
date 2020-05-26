package dtu.openhealth.integration.shared.service.notification

import dtu.openhealth.integration.kafka.producer.IKafkaProducerService
import dtu.openhealth.integration.shared.model.RestEndpoint
import dtu.openhealth.integration.shared.model.ThirdPartyNotification
import dtu.openhealth.integration.shared.model.UserToken
import dtu.openhealth.integration.shared.service.ARetrievingService
import dtu.openhealth.integration.shared.service.data.usertoken.IUserTokenDataService
import dtu.openhealth.integration.shared.service.http.IHttpService
import dtu.openhealth.integration.shared.service.token.refresh.ITokenRefreshService
import io.vertx.core.logging.LoggerFactory


class ThirdPartyNotificationServiceImpl(
        httpService: IHttpService,
        private val endpointMap: Map<String, List<RestEndpoint>>,
        private val userTokenDataService: IUserTokenDataService,
        kafkaProducerService: IKafkaProducerService,
        private val tokenRefreshService: ITokenRefreshService? = null
) : IThirdPartyNotificationService, ARetrievingService(httpService, kafkaProducerService) {

    private val logger = LoggerFactory.getLogger(ThirdPartyNotificationServiceImpl::class.java)

    override suspend fun getUpdatedData(notificationList: List<ThirdPartyNotification>)
    {
        for (notification in notificationList) {
            val extUserId = notification.parameters[notification.userParam]
            val dataType = notification.parameters[notification.dataTypeParam]
            if (extUserId != null && dataType != null) {
                getUserAndCallApi(extUserId, dataType, notification.parameters)
            }
            else {
                val errorMsg = "${notification.userParam} or ${notification.dataTypeParam} not found in ${notification.parameters}."
                logger.error(errorMsg)
            }
        }
    }

    private suspend fun getUserAndCallApi(extUserId: String, dataType: String, parameters: Map<String, String>)
    {
        val userToken = userTokenDataService.getUserByExtId(extUserId)
        if (userToken != null) {
            callApiForUser(userToken, dataType, parameters)
        }
        else {
            logger.error("User $extUserId was not found.")
        }
    }

    private fun callApiForUser(userToken: UserToken, dataType: String, parameters: Map<String, String>)
    {
        if (tokenRefreshService != null && tokenIsExpired(userToken.expireDateTime)) {
            tokenRefreshService.refreshToken(userToken) {
                updatedToken -> callApi(updatedToken, dataType, parameters)
            }
        }
        else {
            callApi(userToken, dataType, parameters)
        }
    }

    private fun callApi(userToken: UserToken, dataType: String, parameters: Map<String, String>)
    {
        val endpointList = endpointMap[dataType]
        if (endpointList != null) {
            callApiAndPublishOmhData(userToken, endpointList, parameters)
        }
        else {
            logger.error("No endpoints configured for $dataType and $userToken. Parameters = $parameters")
        }
    }
}