package dtu.openhealth.integration.service

import dtu.openhealth.integration.data.ThirdPartyData
import dtu.openhealth.integration.model.RestEndpoint
import dtu.openhealth.integration.model.ThirdPartyNotification
import dtu.openhealth.integration.model.User
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json


class ThirdPartyNotificationService(
        private val httpService: HttpService,
        private val endpointMap: Map<String, List<RestEndpoint>>,
        private val userService: UserService) {

    fun getUpdatedData(notificationList: List<ThirdPartyNotification>) {
        for (notification in notificationList) {
            val userId = notification.parameters[notification.userParam]
            val dataType = notification.parameters[notification.dataTypeParam]
            if (userId != null && dataType != null) {
                val user = userService.getUser(userId)
                if (user != null) {
                    callApi(user, dataType, notification.parameters)
                }
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

}