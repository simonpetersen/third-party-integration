package dtu.openhealth.integration.shared.service.impl

import dtu.openhealth.integration.kafka.producer.KafkaProducerService
import dtu.openhealth.integration.shared.model.ThirdPartyData
import dtu.openhealth.integration.shared.model.RestEndpoint
import dtu.openhealth.integration.shared.model.ThirdPartyNotification
import dtu.openhealth.integration.shared.model.UserToken
import dtu.openhealth.integration.shared.service.*
import io.vertx.core.logging.LoggerFactory
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import java.time.LocalDateTime


class ThirdPartyNotificationServiceImpl(
        private val httpService: HttpService,
        private val endpointMap: Map<String, List<RestEndpoint>>,
        private val userDataService: UserDataService,
        private val kafkaProducerService: KafkaProducerService,
        private val tokenRefreshService: TokenRefreshService
) : ThirdPartyNotificationService {

    private val logger = LoggerFactory.getLogger(ThirdPartyNotificationServiceImpl::class.java)
    private val json = Json(JsonConfiguration.Stable)

    override suspend fun getUpdatedData(notificationList: List<ThirdPartyNotification>) {
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

    private suspend fun getUserAndCallApi(extUserId: String, dataType: String, parameters: Map<String, String>) {
        val userToken = userDataService.getUserByExtId(extUserId)
        if (userToken != null) {
            callApiForUser(userToken, dataType, parameters)
        }
        else {
            logger.error("User $extUserId was not found.")
        }
    }

    private suspend fun callApiForUser(userToken: UserToken, dataType: String, parameters: Map<String, String>) {
        if (tokenIsExpired(userToken.expireDateTime)) {
            val updatedUser = tokenRefreshService.refreshToken(userToken)
            callApi(updatedUser, dataType, parameters)
        }
        else {
            callApi(userToken, dataType, parameters)
        }
    }

    private fun callApi(userToken: UserToken, dataType: String, parameters: Map<String, String>) {
        val endpointList = endpointMap[dataType]
        if (endpointList != null) {
            val apiResponseList = httpService.callApiForUser(endpointList,userToken,parameters)

            apiResponseList.subscribe(
                    { result ->
                        result.mapNotNull{
                            convertJsonToThirdPartyData(it.responseJson, it.serializer, json)
                                    ?.mapToOMH(it.parameters)
                        }
                                .forEach { kafkaProducerService.sendOmhData(it) }
                    },
                    { error -> logger.error(error) }
            )
        }
        else {
            logger.error("No endpoints configured for $dataType")
        }
    }

    private fun convertJsonToThirdPartyData(responseJson: String, serializer: KSerializer<out ThirdPartyData>, json: Json) : ThirdPartyData? {
        return try {
            json.parse(serializer, responseJson)
        } catch (e: Exception) {
            logger.error(e)
            null
        }
    }

    private fun tokenIsExpired(expireDateTime: LocalDateTime?): Boolean {
        if (expireDateTime == null) {
            return false
        }

        val now = LocalDateTime.now().minusSeconds(5)
        return expireDateTime.isBefore(now)
    }

}