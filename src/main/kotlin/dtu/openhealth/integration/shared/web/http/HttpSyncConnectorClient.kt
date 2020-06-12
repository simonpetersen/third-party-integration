package dtu.openhealth.integration.shared.web.http

import dtu.openhealth.integration.shared.model.UserToken
import dtu.openhealth.integration.shared.web.ApiRequest
import dtu.openhealth.integration.shared.web.ApiResponse
import io.reactivex.Single
import java.net.HttpURLConnection
import java.net.URL

class HttpSyncConnectorClient : IHttpConnectorClient {
    override fun get(request: ApiRequest, userToken: UserToken): Single<ApiResponse> {
        val url = "https://${request.endpoint.url.host}${request.url}"
        val address = URL(url)
        val connection = address.openConnection() as HttpURLConnection
        val authorization = "Bearer ${userToken.token}"
        connection.setRequestProperty("Authorization", authorization)

        val responseText = connection.inputStream.bufferedReader().readText()
        val apiResponse = ApiResponse(responseText, request.endpoint.serializer, request.parameters)
        return Single.just(apiResponse)
    }
}