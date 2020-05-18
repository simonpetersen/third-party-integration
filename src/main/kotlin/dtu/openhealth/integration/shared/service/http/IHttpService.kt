package dtu.openhealth.integration.shared.service.http

import dtu.openhealth.integration.shared.model.RestEndpoint
import dtu.openhealth.integration.shared.model.UserToken
import dtu.openhealth.integration.shared.web.ApiResponse
import io.reactivex.Single

interface IHttpService {
    fun callApiForUser(endpoints: List<RestEndpoint>, userToken: UserToken, urlParameters: Map<String,String>) : Single<List<ApiResponse>>
}