package dtu.openhealth.integration.web

import dtu.openhealth.integration.data.ThirdPartyData
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import java.net.HttpURLConnection
import java.net.URL

class RestConnector {
    // TODO: Maybe use a better framework for http with OAuth2

    fun get(url: String, serializer: KSerializer<out ThirdPartyData>, token : String) : ThirdPartyData {
        val address = URL(url)
        val connection = address.openConnection() as HttpURLConnection
        val authorization = "Bearer $token"
        connection.setRequestProperty("Authorization", authorization)

        val json = Json(JsonConfiguration.Stable)
        val calories = connection.inputStream.bufferedReader().readText()
        val fitbitData = json.parse(serializer, calories)
        println(fitbitData)
        return fitbitData
    }

    fun post() {
        // TODO: Implement post method. Should be used to post omh data to platform
    }
}