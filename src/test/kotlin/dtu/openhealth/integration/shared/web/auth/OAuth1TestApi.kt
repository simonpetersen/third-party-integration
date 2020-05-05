package dtu.openhealth.integration.shared.web.auth

import com.github.scribejava.core.builder.api.DefaultApi10a

class OAuth1TestApi(private val port: Int) : DefaultApi10a() {

    override fun getRequestTokenEndpoint(): String {
        return "http://localhost:$port/oauth-service/request_token"
    }

    override fun getAuthorizationBaseUrl(): String {
        return "http://localhost:$port/oauthConfirm"
    }

    override fun getAccessTokenEndpoint(): String {
        return "http://localhost:$port/oauth-service/access_token"
    }
}