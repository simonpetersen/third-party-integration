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
        val notificationMap = mapNotificationsByExtUserId(notificationList)
        notificationMap.entries.forEach { handleNotificationList(it.key, it.value)}
    }

    private suspend fun handleNotificationList(extUserId: String, notifications: List<ThirdPartyNotification>)
    {
        val userToken = userTokenDataService.getUserByExtId(extUserId)
        if (userToken != null) {
            callApiForUserList(userToken, notifications)
        }
        else {
            logger.error("User $extUserId was not found.")
        }
    }

    private fun callApiForUserList(userToken: UserToken, notifications: List<ThirdPartyNotification>)
    {
        if (tokenRefreshService != null && tokenIsExpired(userToken.expireDateTime)) {
            tokenRefreshService.refreshToken(userToken) {
                updatedToken -> notifications.forEach { getDataTypeAndCallApi(updatedToken, it) }
            }
        }
        else {
            notifications.forEach { getDataTypeAndCallApi(userToken, it) }
        }
    }

    private fun getDataTypeAndCallApi(userToken: UserToken, notification: ThirdPartyNotification)
    {
        val dataType = notification.parameters[notification.dataTypeParam]
        if (dataType != null) {
            callApi(userToken, dataType, notification.parameters)
        }
        else {
            val errorMsg = "No dataType found for $notification"
            logger.error(errorMsg)
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

    private fun mapNotificationsByExtUserId(notificationList: List<ThirdPartyNotification>): Map<String, List<ThirdPartyNotification>>
    {
        val map = mutableMapOf<String, MutableList<ThirdPartyNotification>>()
        notificationList.forEach { mapNotification(it, map) }
        return map
    }

    private fun mapNotification(notification: ThirdPartyNotification, map: MutableMap<String, MutableList<ThirdPartyNotification>>)
    {
        val extUserId = notification.parameters[notification.userParam]
        if (extUserId != null) {
            addNotificationToMap(extUserId, notification, map)
        }
        else {
            val errorMsg = "ExtUserId not found in $notification."
            logger.error(errorMsg)
        }
    }

    private fun addNotificationToMap(extUserId: String, notification: ThirdPartyNotification, map: MutableMap<String, MutableList<ThirdPartyNotification>>)
    {
        if (map.containsKey(extUserId)) {
            map[extUserId]?.add(notification)
        }
        else {
            map[extUserId] = mutableListOf(notification)
        }
    }
}