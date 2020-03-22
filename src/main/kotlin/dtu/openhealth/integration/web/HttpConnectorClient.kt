package dtu.openhealth.integration.web

import dtu.openhealth.integration.model.RestEndpoint
import io.reactivex.Single

interface HttpConnectorClient {
    fun get(endpoint:RestEndpoint, url: String, token : String) : Single<ApiResponse>
    fun post(url: String)
}