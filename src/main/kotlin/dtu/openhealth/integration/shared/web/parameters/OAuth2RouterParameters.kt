package dtu.openhealth.integration.shared.web.parameters

data class OAuth2RouterParameters(
        val redirectUri: String,
        val returnUri: String,
        val scope: String
)