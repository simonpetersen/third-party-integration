package dtu.openhealth.integration.shared.service.token.revoke.data

data class OAuth2RevokeParameters(
        val host: String,
        val revokeUrl: String,
        val subscriptionUrl: String,
        val clientId: String,
        val clientSecret: String,
        val port: Int,
        val ssl: Boolean = true
)