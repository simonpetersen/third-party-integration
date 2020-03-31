package dtu.openhealth.integration.shared.web

data class TestRestUrl(val hostName: String, override var uri: String) : RestUrl(hostName, uri)