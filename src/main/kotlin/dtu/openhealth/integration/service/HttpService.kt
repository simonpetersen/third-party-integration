package dtu.openhealth.integration.service

import dtu.openhealth.integration.data.ThirdPartyData
import dtu.openhealth.integration.model.User
import io.reactivex.Single

interface HttpService {
    fun callApiForUser(user: User, urlParameters: Map<String,String>) : Single<List<ThirdPartyData>>
}