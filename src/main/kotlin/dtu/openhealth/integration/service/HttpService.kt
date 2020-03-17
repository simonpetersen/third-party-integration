package dtu.openhealth.integration.service

import dtu.openhealth.integration.data.ThirdPartyData
import dtu.openhealth.integration.model.User

interface HttpService {
    fun callApiForUser(user: User, urlParameters: Map<String,String>) : List<ThirdPartyData>
}