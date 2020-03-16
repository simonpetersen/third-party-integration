package dtu.openhealth.integration.service

import dtu.openhealth.integration.data.ThirdPartyData
import dtu.openhealth.integration.fitbit.data.FitbitActivitiesSummary
import dtu.openhealth.integration.fitbit.mapping.FitbitMapper
import dtu.openhealth.integration.model.RestEndpoint
import dtu.openhealth.integration.model.ThirdPartyNotification
import dtu.openhealth.integration.model.User
import dtu.openhealth.integration.web.HttpOAuth2Connector
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration

class RestConnectorService {

    private val urlMap = HashMap<String, RestEndpoint>()
    private val userTokens = HashMap<String, User>() // Temp mock DB with HashMaps

    private val httpConnector = HttpOAuth2Connector()
    private val fitbitMapper = FitbitMapper() // Should be extracted to MappingService

    init {
        //val endpoint = RestEndpoint("https://api.fitbit.com/1/user/[userId]/activities/calories/date/[date]/1d.json", FitbitActivitiesCalories.serializer())
        val endpoint = RestEndpoint("https://api.fitbit.com/1/user/[ownerId]/activities/date/[date].json", FitbitActivitiesSummary.serializer())
        urlMap["activities"] = endpoint
        userTokens["89NGPS"] = User("89NGPS", "123", "123")
    }

    fun retrieveDataForUser(notification: ThirdPartyNotification) {
        val userId = notification.parameters[notification.userParam]
        val dataType = notification.parameters[notification.dataTypeParam]
        if (userId != null && dataType != null) {
            val user = userTokens[userId]
            if (user != null) {
                val endpoint = urlMap[dataType]
                if (endpoint != null) {
                    addUrlParamsAndCallApi(endpoint, notification, user.token)
                }
            }
        }
    }

    private fun addUrlParamsAndCallApi(endpoint: RestEndpoint, notification: ThirdPartyNotification, userToken: String) {
        val regex = Regex("\\[(.*?)\\]")
        var url = endpoint.url
        val parameters = regex.findAll(url).map { it.groupValues[1] }.toList()
        for (parameter in parameters) {
            val parameterValue: Any? = notification.parameters[parameter]
            if (parameterValue != null && parameterValue is String) {
                url = url.replace("[$parameter]", parameterValue)
            }
        }

        val responseJson = httpConnector.get(url, userToken)
        val thirdPartyData = convertJsonToThirdPartyData(responseJson, endpoint.serializer)
        val omhData = fitbitMapper.mapData(thirdPartyData)
        println(omhData)
    }

    private fun convertJsonToThirdPartyData(responseJson: String, serializer: KSerializer<out ThirdPartyData>) : ThirdPartyData {
        val json = Json(JsonConfiguration.Stable)
        val thirdPartyData = json.parse(serializer, responseJson)
        println(thirdPartyData)
        return thirdPartyData
    }
}