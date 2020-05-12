package dtu.openhealth.integration.shared.service.impl

import dtu.openhealth.integration.shared.model.RestEndpoint
import dtu.openhealth.integration.shared.model.UserToken
import dtu.openhealth.integration.shared.service.HttpService
import dtu.openhealth.integration.shared.web.ApiRequest
import dtu.openhealth.integration.shared.web.ApiResponse
import dtu.openhealth.integration.shared.web.HttpConnectorClient
import io.reactivex.Single
import io.vertx.core.logging.LoggerFactory

class HttpServiceImpl(private val httpClient: HttpConnectorClient) : HttpService {

    private val logger = LoggerFactory.getLogger(HttpServiceImpl::class.java)

    override fun callApiForUser(endpoints: List<RestEndpoint>, userToken: UserToken, urlParameters: Map<String,String>) : Single<List<ApiResponse>> {
        val singles = endpoints.map { addUrlParamsAndCallApi(it, urlParameters, userToken) }

        return Single.zip(singles) {
            it.filterIsInstance<ApiResponse>()
        }
    }

    private fun addUrlParamsAndCallApi(endpoint: RestEndpoint, urlParameters: Map<String, String>, userToken: UserToken): Single<ApiResponse> {
        val regex = Regex("\\[(.*?)\\]")
        val apiParameters = mutableMapOf<String,String>()
        var url = endpoint.url.uri
        val parameters = regex.findAll(url).map { it.groupValues[1] }.toList()
        for (parameter in parameters) {
            val parameterValue: String? = urlParameters[parameter]
            if (parameterValue != null) {
                url = url.replace("[$parameter]", parameterValue)
                apiParameters[parameter] = parameterValue
            }
            else {
                val errorMsg = "Parameter $parameter not found in $urlParameters"
                logger.error(errorMsg)
            }
        }

        val request = ApiRequest(endpoint, url, apiParameters)
        return httpClient.get(request, userToken)
    }
}