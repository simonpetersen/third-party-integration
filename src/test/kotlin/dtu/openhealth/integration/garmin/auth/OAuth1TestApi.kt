package dtu.openhealth.integration.garmin.auth

import com.github.scribejava.core.builder.api.DefaultApi10a

class OAuth1TestApi(
        port: Int
): DefaultApi10a() {

    private val baseApiPath = "http://localhost:$port"
    private val authorisationPath = "/oauthConfirm"
    private val requestTokenPath = "/oauth-service/request_token"
    private val accessTokenPath = "/oauth-service/access_token"

    override fun getRequestTokenEndpoint(): String {
        return "$baseApiPath$requestTokenPath"
    }

    override fun getAuthorizationBaseUrl(): String {
        return "$baseApiPath$authorisationPath"
    }

    override fun getAccessTokenEndpoint(): String {
        return "$baseApiPath$accessTokenPath"
    }
}