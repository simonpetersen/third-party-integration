package dtu.openhealth.integration.shared.web.auth

import com.github.scribejava.core.builder.api.DefaultApi10a

class GarminApi : DefaultApi10a() {

    private val baseApiPath = "https://connectapi.garmin.com"
    private val authorizationPath = "/oauthConfirm"
    private val requestTokenPath = "/oauth-service/oauth/request_token"
    private val accessTokenPath = "/oauth-service/oauth/access_token"

    override fun getRequestTokenEndpoint(): String {
        return "$baseApiPath$requestTokenPath"
    }

    override fun getAuthorizationBaseUrl(): String {
        return "$baseApiPath$authorizationPath"
    }

    override fun getAccessTokenEndpoint(): String {
        return "$baseApiPath$accessTokenPath"
    }
}