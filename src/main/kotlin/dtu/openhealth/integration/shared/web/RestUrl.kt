package dtu.openhealth.integration.shared.web

abstract class RestUrl(
        val host: String,
        open var uri: String
)