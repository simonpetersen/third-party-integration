package dtu.openhealth.integration.web

import dtu.openhealth.integration.common.exception.ThirdPartyConnectionException
import java.net.HttpURLConnection
import java.net.URL

class HttpOAuth2Connector : HttpConnector {
    // TODO: Maybe use a better framework for http with OAuth2

    override fun get(url: String, token : String) : String {
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

    override fun post(url: String) {
        // TODO: Implement post method. Should be used to post omh data to platform
    }
}