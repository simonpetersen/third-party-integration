package dtu.openhealth.integration.shared.service

import dtu.openhealth.integration.shared.model.RestEndpoint
import dtu.openhealth.integration.shared.model.UserToken
import dtu.openhealth.integration.shared.web.ApiResponse
import io.reactivex.Single

interface HttpService {
    fun callApiForUser(endpoints: List<RestEndpoint>, userToken: UserToken, urlParameters: Map<String,String>) : Single<List<ApiResponse>>
}