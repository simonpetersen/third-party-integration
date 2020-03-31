package dtu.openhealth.integration.shared.service.mock

import dtu.openhealth.integration.shared.web.RestUrl

data class MockRestUrl(override var uri: String) : RestUrl("api.anycompany.com", uri)