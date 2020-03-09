package dtu.openhealth.integration.service.impl

import dtu.openhealth.integration.data.ThirdPartyData
import dtu.openhealth.integration.data.fitbit.FitbitActivitiesSummary
import dtu.openhealth.integration.mapping.FitbitMapper
import dtu.openhealth.integration.model.RestEndpoint
import dtu.openhealth.integration.model.ThirdPartyNotification
import dtu.openhealth.integration.model.User
import dtu.openhealth.integration.service.RestConnectorService
import dtu.openhealth.integration.web.HttpConnector
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import org.springframework.stereotype.Service

@Service
class RestConnectorServiceImpl : RestConnectorService {

    private val urlMap = HashMap<String, RestEndpoint>()
    private val userTokens = HashMap<String, User>() // Temp mock DB with HashMaps

    private val httpConnector = HttpConnector()
    private val fitbitMapper = FitbitMapper() // Should be extracted to MappingService

    init {
        //val endpoint = RestEndpoint(listOf("https://api.fitbit.com/1/user/[userId]/activities/calories/date/[date]/1d.json"), FitbitActivitiesCalories.serializer())
        val endpoint = RestEndpoint(listOf("https://api.fitbit.com/1/user/[ownerId]/activities/date/[date].json"), FitbitActivitiesSummary.serializer())
        urlMap["activities"] = endpoint
        userTokens["89NGPS"] = User("89NGPS", "abc", "123")
    }

    override fun retrieveDataForUser(notification: ThirdPartyNotification) {
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
        for (url in endpoint.urls) {
            val regex = Regex("\\[(.*?)\\]")
            val parameters = regex.findAll(url).map { it.groupValues[1] }.toList()
            var modUrl = url
            for (parameter in parameters) {
                val parameterValue: String? = notification.parameters[parameter]
                if (parameterValue != null) {
                    modUrl = modUrl.replace("[$parameter]", parameterValue)
                }
            }

            val responseJson = httpConnector.get(modUrl, userToken)
            val thirdPartyData = convertJsonToThirdPartyData(responseJson, endpoint.serializer)
            val omhData = fitbitMapper.mapData(thirdPartyData)
            println(omhData)
        }
    }

    private fun convertJsonToThirdPartyData(responseJson: String, serializer: KSerializer<out ThirdPartyData>) : ThirdPartyData {
        val json = Json(JsonConfiguration.Stable)
        val thirdPartyData = json.parse(serializer, responseJson)
        println(thirdPartyData)
        return thirdPartyData
    }
}