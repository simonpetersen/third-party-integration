package dtu.openhealth.integration.garmin.auth

import com.github.scribejava.core.builder.api.DefaultApi10a

class GarminApi : DefaultApi10a() {

    private val baseApiPath = "https://connectapi.garmin.com"
    private val requestTokenPath = "/oauth-service/oauth/request_token"
    private val accessTokenPath = "/oauth-service/oauth/access_token"

    override fun getRequestTokenEndpoint(): String {
        return "$baseApiPath$requestTokenPath"
    }

    override fun getAuthorizationBaseUrl(): String {
        return "https://connect.garmin.com/oauthConfirm"
    }

    override fun getAccessTokenEndpoint(): String {
        return "$baseApiPath$accessTokenPath"
    }
}