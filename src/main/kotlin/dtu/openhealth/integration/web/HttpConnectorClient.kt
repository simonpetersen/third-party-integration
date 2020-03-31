package dtu.openhealth.integration.web

import dtu.openhealth.integration.model.RestEndpoint
import io.reactivex.Single

interface HttpConnectorClient {
    fun get(request: ApiRequest, token : String) : Single<ApiResponse>
    fun post(url: String)
}