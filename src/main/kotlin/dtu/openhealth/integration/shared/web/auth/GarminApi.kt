package dtu.openhealth.integration.shared.web.auth

import com.github.scribejava.core.builder.api.DefaultApi10a

class GarminApi : DefaultApi10a() {

    private object InstanceHolder {
        val INSTANCE: GarminApi = GarminApi()
    }

    companion object {
        fun instance(): GarminApi {
            return InstanceHolder.INSTANCE
        }
    }

    override fun getRequestTokenEndpoint(): String {
        return "https://connectapi.garmin.com/oauth-service/oauth/request_token"
    }

    override fun getAuthorizationBaseUrl(): String {
        return "https://connect.garmin.com/oauthConfirm"
    }

    override fun getAccessTokenEndpoint(): String {
        return "https://connectapi.garmin.com/oauth-service/oauth/access_token"
    }
}