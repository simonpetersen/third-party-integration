package dtu.openhealth.integration.shared.web

import dtu.openhealth.integration.shared.util.exception.ThirdPartyConnectionException
import dtu.openhealth.integration.fitbit.data.FitbitActivitiesCalories
import io.reactivex.Single
import io.vertx.core.AsyncResult
import io.vertx.reactivex.ext.web.client.WebClient
import io.vertx.reactivex.ext.web.client.HttpResponse
import io.vertx.reactivex.ext.web.client.predicate.ResponsePredicate
import io.vertx.reactivex.ext.web.codec.BodyCodec
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import java.net.HttpURLConnection
import java.net.URL


class HttpOAuth2ConnectorClient(private val webClient: WebClient, private val port: Int = 443) : HttpConnectorClient {

    fun getOld(url: String, token : String) : String {
        val address = URL(url)
        val connection = address.openConnection() as HttpURLConnection
        val authorization = "Bearer $token"
        connection.setRequestProperty("Authorization", authorization)

        try {
            return connection.inputStream.bufferedReader().readText()
        } catch (e: Exception) {
            throw ThirdPartyConnectionException(e.message ?: "Error connecting to Third Party API")
        }
    }

    private fun getHandler(ar: AsyncResult<HttpResponse<String>>) {
        if (ar.succeeded()) {
            val response = ar.result()
            val body = response.body()
            val json = Json(JsonConfiguration.Stable)
            val fitbitData = json.parse(FitbitActivitiesCalories.serializer(), body)
            println(fitbitData)
        }
        else {
            println("Something went wrong ${ar.cause().message}")
        }
    }

    override fun get(request: ApiRequest, token: String): Single<ApiResponse> {
        return webClient.get(port, request.endpoint.url.host, request.url)
                .ssl(true)
                .bearerTokenAuthentication(token)
                .expect(ResponsePredicate.SC_SUCCESS)
                .`as`(BodyCodec.string())
                .rxSend()
                .map { ApiResponse(it.body(), request.endpoint.serializer, request.parameters) }
    }

    override fun post(url: String) {
        // TODO: Implement post method. Should be used to post omh data to platform
    }
}