package dtu.openhealth.integration.fitbit

import dtu.openhealth.integration.model.RestEndpoint
import dtu.openhealth.integration.model.User
import dtu.openhealth.integration.service.HttpService
import dtu.openhealth.integration.service.ThirdPartyPullService
import dtu.openhealth.integration.service.UserService
import java.time.LocalDate

class FitbitPullService(httpService: HttpService, endpointList: List<RestEndpoint>, private val userService: UserService)
    : ThirdPartyPullService(httpService, endpointList) {

    override fun getUserList(): List<User> {
        return userService.getAllUsers()
    }

    override fun getUserParameters(user: User): Map<String, String> {
        val parameters = HashMap<String, String>()

        parameters["userId"] = user.userId
        parameters["date"] = LocalDate.now().toString()

        return parameters
    }
}