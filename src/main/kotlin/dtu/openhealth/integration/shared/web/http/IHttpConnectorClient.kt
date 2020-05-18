package dtu.openhealth.integration.shared.web.http

import dtu.openhealth.integration.shared.model.UserToken
import dtu.openhealth.integration.shared.web.ApiRequest
import dtu.openhealth.integration.shared.web.ApiResponse
import io.reactivex.Single

interface IHttpConnectorClient {
    fun get(request: ApiRequest, userToken : UserToken) : Single<ApiResponse>
}