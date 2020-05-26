package dtu.openhealth.integration.shared.service.token.revoke.data

import com.github.scribejava.core.builder.api.DefaultApi10a

data class OAuth1RevokeParameters(
        val host: String,
        val revokeUrl : String,
        val consumerKey: String,
        val consumerSecret: String,
        val port: Int,
        val api: DefaultApi10a,
        val ssl: Boolean = true
)
