package dtu.openhealth.integration.shared.web

import io.reactivex.Single

interface HttpConnectorClient {
    fun get(request: ApiRequest, token : String) : Single<ApiResponse>
    fun post(url: String)
}