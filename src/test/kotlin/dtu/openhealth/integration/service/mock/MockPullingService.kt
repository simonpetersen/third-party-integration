package dtu.openhealth.integration.service.mock

import dtu.openhealth.integration.mapping.ThirdPartyMapper
import dtu.openhealth.integration.model.RestEndpoint
import dtu.openhealth.integration.model.User
import dtu.openhealth.integration.service.ThirdPartyPullingService
import dtu.openhealth.integration.web.HttpConnector

class MockPullingService(
        mapper: ThirdPartyMapper,
        endpoints: List<RestEndpoint>,
        httpConnector: HttpConnector
): ThirdPartyPullingService(mapper, endpoints, httpConnector) {

    override fun getUrlParameters(user: User): Map<String, String> {
        return HashMap()
    }
}