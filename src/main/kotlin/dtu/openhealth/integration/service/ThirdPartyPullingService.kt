package dtu.openhealth.integration.service

import dtu.openhealth.integration.common.exception.ThirdPartyConnectionException
import dtu.openhealth.integration.data.ThirdPartyData
import dtu.openhealth.integration.mapping.ThirdPartyMapper
import dtu.openhealth.integration.model.RestEndpoint
import dtu.openhealth.integration.model.User
import dtu.openhealth.integration.web.HttpConnector
import dtu.openhealth.integration.web.HttpOAuth2Connector
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration

abstract class ThirdPartyPullingService(
        private val mapper: ThirdPartyMapper,
        private val endpoints: List<RestEndpoint>,
        private val httpConnector: HttpConnector) {

    fun pullData() {
        val users = listOf(User("89NGPS", "123", "123")) // TODO: Query users from DB
        users.forEach { callEndpointsForUser(it) }
    }

    private fun callEndpointsForUser(user: User) {
        val urlParameters = getUrlParameters(user)
        try {
            for (endpoint in endpoints) {
                val responseJson = addUrlParamsAndCallApi(endpoint, urlParameters, user.token)
                val thirdPartyData = convertJsonToThirdPartyData(responseJson, endpoint.serializer)
                val omhData = mapper.mapData(thirdPartyData)
                println(omhData) // TODO: Put on Kafka stream.
            }
        } catch(e: ThirdPartyConnectionException) {
            println(e)
        }
    }

    private fun addUrlParamsAndCallApi(endpoint: RestEndpoint, urlParameters: Map<String, String>, userToken: String): String {
        val regex = Regex("\\[(.*?)\\]")
        var url = endpoint.url
        val parameters = regex.findAll(url).map { it.groupValues[1] }.toList()
        for (parameter in parameters) {
            val parameterValue: String? = urlParameters[parameter]
            if (parameterValue != null) { // TODO: Handle missing parameter.
                url = url.replace("[$parameter]", parameterValue)
            }
        }

        return httpConnector.get(url, userToken)

    }

    private fun convertJsonToThirdPartyData(responseJson: String, serializer: KSerializer<out ThirdPartyData>) : ThirdPartyData {
        val json = Json(JsonConfiguration.Stable)
        val thirdPartyData = json.parse(serializer, responseJson)
        println(thirdPartyData)
        return thirdPartyData
    }

    abstract fun getUrlParameters(user: User): Map<String, String>
}