package dtu.openhealth.integration.fitbit

import dtu.openhealth.integration.model.User
import dtu.openhealth.integration.service.HttpService
import dtu.openhealth.integration.service.ThirdPartyPullService
import java.time.LocalDate

class FitbitPullService(httpService: HttpService) : ThirdPartyPullService(httpService) {

    override fun getUserList(): List<User> {
        // TODO: Call list from UserService
        return listOf(User("89NGPS", "eyJhbGciOiJIUzI1NiJ9.eyJhdWQiOiIyMkI5QlciLCJzdWIiOiI4OU5HUFMiLCJpc3MiOiJGaXRiaXQiLCJ0eXAiOiJhY2Nlc3NfdG9rZW4iLCJzY29wZXMiOiJ3aHIgd251dCB3cHJvIHdzbGUgd3dlaSB3c29jIHdhY3Qgd3NldCIsImV4cCI6MTU4NDY0MTI4OSwiaWF0IjoxNTg0NjEyNDg5fQ.hy4IQkcHnspOlKeBnmv3r2nu8X63kVbGVXtCRms8s54", "123"))
    }

    override fun getUserParameters(user: User): Map<String, String> {
        val parameters = HashMap<String, String>()

        parameters["userId"] = user.userId
        parameters["date"] = LocalDate.now().toString()

        return parameters
    }
}