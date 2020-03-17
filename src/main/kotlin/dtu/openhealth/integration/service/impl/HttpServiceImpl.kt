package dtu.openhealth.integration.service.impl

import dtu.openhealth.integration.data.ThirdPartyData
import dtu.openhealth.integration.model.RestEndpoint
import dtu.openhealth.integration.model.User
import dtu.openhealth.integration.service.HttpService
import dtu.openhealth.integration.web.HttpConnectorClient
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration

class HttpServiceImpl(private val httpClient: HttpConnectorClient, private val endpoints: List<RestEndpoint>) : HttpService {

    override fun callApiForUser(user: User, urlParameters: Map<String,String>) : List<ThirdPartyData> {
        val data = mutableListOf<ThirdPartyData>()

        for (endpoint in endpoints) {
            val responseJson = addUrlParamsAndCallApi(endpoint, urlParameters, user.token)
            val thirdPartyData = convertJsonToThirdPartyData(responseJson, endpoint.serializer)
            data.add(thirdPartyData)
        }

        return data
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

        return httpClient.get(url, userToken)

    }

    private fun convertJsonToThirdPartyData(responseJson: String, serializer: KSerializer<out ThirdPartyData>) : ThirdPartyData {
        val json = Json(JsonConfiguration.Stable)
        val thirdPartyData = json.parse(serializer, responseJson)
        println(thirdPartyData)
        return thirdPartyData
    }
}