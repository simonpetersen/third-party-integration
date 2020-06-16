package dtu.openhealth.integration.shared.service.mock

import dtu.openhealth.integration.shared.web.RestUrl

data class LocalhostRestUrl(override var uri: String) : RestUrl("localhost", uri)