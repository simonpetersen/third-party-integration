package dtu.openhealth.integration.fitbit

import dtu.openhealth.integration.mapping.ThirdPartyMapper
import dtu.openhealth.integration.model.RestEndpoint
import dtu.openhealth.integration.model.User
import dtu.openhealth.integration.service.ThirdPartyPullingService
import dtu.openhealth.integration.web.HttpOAuth2Connector
import java.time.LocalDate

class FitbitPullingService(mapper: ThirdPartyMapper,
                           endpoints: List<RestEndpoint>,
                           httpOAuth2Connector: HttpOAuth2Connector) : ThirdPartyPullingService(mapper, endpoints, httpOAuth2Connector) {

    override fun getUrlParameters(user: User): Map<String, String> {
        val parameters = HashMap<String, String>()

        parameters["userId"] = user.userId
        parameters["date"] = LocalDate.now().toString()

        return parameters
    }
}