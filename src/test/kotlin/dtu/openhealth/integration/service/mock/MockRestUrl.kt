package dtu.openhealth.integration.service.mock

import dtu.openhealth.integration.web.RestUrl

data class MockRestUrl(override var uri: String) : RestUrl("api.anycompany.com", uri)