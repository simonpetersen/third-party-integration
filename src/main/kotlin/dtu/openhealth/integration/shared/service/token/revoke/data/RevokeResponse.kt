package dtu.openhealth.integration.shared.service.token.revoke.data

/**
 * Data Class representing the API response for a given user
 */
data class RevokeResponse(
        val userId: String,
        val statusCode: Int,
        val responseBody: String? = null
)