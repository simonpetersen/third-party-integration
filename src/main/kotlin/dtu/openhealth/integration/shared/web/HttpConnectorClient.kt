package dtu.openhealth.integration.shared.web

import dtu.openhealth.integration.shared.model.UserToken
import io.reactivex.Single

interface HttpConnectorClient {
    fun get(request: ApiRequest, userToken : UserToken) : Single<ApiResponse>
}