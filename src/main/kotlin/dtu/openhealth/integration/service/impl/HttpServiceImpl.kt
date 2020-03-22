package dtu.openhealth.integration.service.impl

import dtu.openhealth.integration.data.ThirdPartyData
import dtu.openhealth.integration.model.RestEndpoint
import dtu.openhealth.integration.model.User
import dtu.openhealth.integration.service.HttpService
import dtu.openhealth.integration.web.ApiResponse
import dtu.openhealth.integration.web.HttpConnectorClient
import io.reactivex.Single
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration

class HttpServiceImpl(private val httpClient: HttpConnectorClient, private val endpoints: List<RestEndpoint>) : HttpService {

    override fun callApiForUser(user: User, urlParameters: Map<String,String>) : Single<List<ThirdPartyData>> {
        val singles = endpoints.map { addUrlParamsAndCallApi(it, urlParameters, user.token) }.toList()

        return Single.zip(singles) { zipSingles(it) }
    }

    private fun zipSingles(values: Array<Any>): List<ThirdPartyData> {
        val json = Json(JsonConfiguration.Stable)
        val thirdPartyData = mutableListOf<ThirdPartyData>()
        for (value in values) {
            if (value is ApiResponse) {
                val parsedData = convertJsonToThirdPartyData(value.responseJson, value.serializer, json)
                if (parsedData != null) {
                    thirdPartyData.add(parsedData)
                }
            }
        }

        return thirdPartyData
    }

    private fun addUrlParamsAndCallApi(endpoint: RestEndpoint, urlParameters: Map<String, String>, userToken: String): Single<ApiResponse> {
        val regex = Regex("\\[(.*?)\\]")
        var url = endpoint.url.uri
        val parameters = regex.findAll(url).map { it.groupValues[1] }.toList()
        for (parameter in parameters) {
            val parameterValue: String? = urlParameters[parameter]
            if (parameterValue != null) { // TODO: Handle missing parameter.
                url = url.replace("[$parameter]", parameterValue)
            }
        }

        return httpClient.get(endpoint, url, userToken)
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