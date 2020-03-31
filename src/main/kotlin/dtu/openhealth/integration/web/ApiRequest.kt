package dtu.openhealth.integration.web

import dtu.openhealth.integration.model.RestEndpoint

data class ApiRequest(val endpoint: RestEndpoint,
                      val url: String,
                      val parameters: Map<String, String>)