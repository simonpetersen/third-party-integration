package dtu.openhealth.integration.shared.web.parameters

data class OAuth2RefreshParameters(
        val host: String,
        val refreshPath: String,
        val clientId: String,
        val clientSecret: String,
        val port: Int
)