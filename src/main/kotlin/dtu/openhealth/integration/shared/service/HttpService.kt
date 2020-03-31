package dtu.openhealth.integration.shared.service

import dtu.openhealth.integration.shared.model.RestEndpoint
import dtu.openhealth.integration.shared.model.User
import dtu.openhealth.integration.shared.web.ApiResponse
import io.reactivex.Single

interface HttpService {
    fun callApiForUser(endpoints: List<RestEndpoint>, user: User, urlParameters: Map<String,String>) : Single<List<ApiResponse>>
}