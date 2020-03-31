package dtu.openhealth.integration.service

import dtu.openhealth.integration.data.ThirdPartyData
import dtu.openhealth.integration.model.RestEndpoint
import dtu.openhealth.integration.model.User
import dtu.openhealth.integration.web.ApiRequest
import dtu.openhealth.integration.web.ApiResponse
import io.reactivex.Single

interface HttpService {
    fun callApiForUser(endpoints: List<RestEndpoint>, user: User, urlParameters: Map<String,String>) : Single<List<ApiResponse>>
}