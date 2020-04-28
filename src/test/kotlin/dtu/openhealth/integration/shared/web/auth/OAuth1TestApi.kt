package dtu.openhealth.integration.shared.web.auth

import com.github.scribejava.core.builder.api.DefaultApi10a

class OAuth1TestApi : DefaultApi10a() {
    private object InstanceHolder {
        val INSTANCE: OAuth1TestApi = OAuth1TestApi()
    }

    companion object {
        fun instance(): OAuth1TestApi {
            return InstanceHolder.INSTANCE
        }
    }

    override fun getRequestTokenEndpoint(): String {
        return "http://localhost:8083/oauth-service/request_token"
    }

    override fun getAuthorizationBaseUrl(): String {
        return "http://localhost:8083/oauthConfirm"
    }

    override fun getAccessTokenEndpoint(): String {
        return "http://localhost:8083/oauth-service/access_token"
    }
}
