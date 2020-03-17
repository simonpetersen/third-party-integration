package dtu.openhealth.integration.fitbit

import dtu.openhealth.integration.mapping.ThirdPartyMapper
import dtu.openhealth.integration.model.User
import dtu.openhealth.integration.service.HttpService
import dtu.openhealth.integration.service.ThirdPartyPullingService
import java.time.LocalDate

class FitbitPullingService(mapper: ThirdPartyMapper,
                           httpService: HttpService) : ThirdPartyPullingService(mapper,httpService) {

    override fun getUserList(): List<User> {
        return listOf(User("89NGPS", "123", "123"))
    }

    override fun getUserParameters(user: User): Map<String, String> {
        val parameters = HashMap<String, String>()

        parameters["userId"] = user.userId
        parameters["date"] = LocalDate.now().toString()

        return parameters
    }
}