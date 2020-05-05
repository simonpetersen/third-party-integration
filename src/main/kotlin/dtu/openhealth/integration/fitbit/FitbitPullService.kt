package dtu.openhealth.integration.fitbit

import dtu.openhealth.integration.shared.model.RestEndpoint
import dtu.openhealth.integration.shared.model.UserToken
import dtu.openhealth.integration.shared.service.HttpService
import dtu.openhealth.integration.shared.service.ThirdPartyPullService
import dtu.openhealth.integration.shared.service.UserDataService
import java.time.LocalDate

class FitbitPullService(httpService: HttpService,
                        endpointList: List<RestEndpoint>,
                        private val userService: UserDataService)
    : ThirdPartyPullService(httpService, endpointList) {

    override fun getUserList(): List<UserToken> {
        // Use userDataService
        return emptyList()
    }

    override fun getUserParameters(userToken: UserToken): Map<String, String> {
        val parameters = HashMap<String, String>()

        parameters["userId"] = userToken.userId
        parameters["date"] = LocalDate.now().toString()

        return parameters
    }
}