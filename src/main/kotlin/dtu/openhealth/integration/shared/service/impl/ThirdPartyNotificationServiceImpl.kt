package dtu.openhealth.integration.shared.service.impl

import dtu.openhealth.integration.shared.data.ThirdPartyData
import dtu.openhealth.integration.shared.model.RestEndpoint
import dtu.openhealth.integration.shared.model.ThirdPartyNotification
import dtu.openhealth.integration.shared.model.User
import dtu.openhealth.integration.shared.service.*
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json
import java.time.LocalDateTime


class ThirdPartyNotificationServiceImpl(
        private val httpService: HttpService,
        private val endpointMap: Map<String, List<RestEndpoint>>,
        private val userService: UserDataService,
        private val tokenRefreshService: TokenRefreshService
) : ThirdPartyNotificationService {

    override suspend fun getUpdatedData(notificationList: List<ThirdPartyNotification>) {
        for (notification in notificationList) {
            val extUserId = notification.parameters[notification.userParam]
            val dataType = notification.parameters[notification.dataTypeParam]
            if (extUserId != null && dataType != null) {
                getUserAndCallApi(extUserId, dataType, notification.parameters)
            }
        }
    }

    private suspend fun getUserAndCallApi(extUserId: String, dataType: String, parameters: Map<String, String>) {
        val user = userService.getUserByExtId(extUserId)
        if (user != null) {
            if (tokenIsExpired(user.expireDateTime)) {
                val updatedUser = tokenRefreshService.refreshToken(user)
                callApi(updatedUser, dataType, parameters)
            }
            else {
                callApi(user, dataType, parameters)
            }
        }
    }

    private fun callApi(user: User, dataType: String, parameters: Map<String, String>) {
        val endpointList = endpointMap[dataType]
        if (endpointList != null) {
            val apiResponseList = httpService.callApiForUser(endpointList,user,parameters)

            // TODO: Put result on Kafka stream.
            apiResponseList.subscribe(
                    { result -> println("Result = $result") },
                    { error -> println(error) }
            )
        }
    }

    private fun convertJsonToThirdPartyData(responseJson: String, serializer: KSerializer<out ThirdPartyData>, json: Json) : ThirdPartyData? {
        return try {
            json.parse(serializer, responseJson)
        } catch (e: Exception) {
            println(e)
            null
        }
    }

    private fun tokenIsExpired(expireDateTime: LocalDateTime?): Boolean {
        if (expireDateTime == null) {
            return false
        }

        val now = LocalDateTime.now().minusSeconds(5)
        return expireDateTime.isAfter(now)
    }

}