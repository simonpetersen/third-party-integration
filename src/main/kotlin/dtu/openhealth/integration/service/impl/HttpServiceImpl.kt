package dtu.openhealth.integration.service.impl

import dtu.openhealth.integration.data.ThirdPartyData
import dtu.openhealth.integration.model.RestEndpoint
import dtu.openhealth.integration.model.User
import dtu.openhealth.integration.service.HttpService
import dtu.openhealth.integration.web.ApiRequest
import dtu.openhealth.integration.web.ApiResponse
import dtu.openhealth.integration.web.HttpConnectorClient
import io.reactivex.Single
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration

class HttpServiceImpl(private val httpClient: HttpConnectorClient) : HttpService {

    override fun callApiForUser(endpoints: List<RestEndpoint>, user: User, urlParameters: Map<String,String>) : Single<List<ApiResponse>> {
        val singles = endpoints.map { addUrlParamsAndCallApi(it, urlParameters, user.token) }.toList()

        return Single.zip(singles) { combineSingles(it) }
    }

    private fun combineSingles(values: Array<Any>): List<ApiResponse> {
        //val json = Json(JsonConfiguration.Stable)
        return values.filterIsInstance<ApiResponse>()
    }

    private fun addUrlParamsAndCallApi(endpoint: RestEndpoint, urlParameters: Map<String, String>, userToken: String): Single<ApiResponse> {
        val regex = Regex("\\[(.*?)\\]")
        val apiParameters = mutableMapOf<String,String>()
        var url = endpoint.url.uri
        val parameters = regex.findAll(url).map { it.groupValues[1] }.toList()
        for (parameter in parameters) {
            val parameterValue: String? = urlParameters[parameter]
            if (parameterValue != null) { // TODO: Handle missing parameter.
                url = url.replace("[$parameter]", parameterValue)
                apiParameters[parameter] = parameterValue
            }
        }

        val request = ApiRequest(endpoint, url, apiParameters)
        return httpClient.get(request, userToken)
    }
}