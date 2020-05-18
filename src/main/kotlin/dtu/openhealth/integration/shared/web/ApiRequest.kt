package dtu.openhealth.integration.shared.web

import dtu.openhealth.integration.shared.model.RestEndpoint

data class ApiRequest(
        val endpoint: RestEndpoint,
        val url: String,
        val parameters: Map<String, String>
)