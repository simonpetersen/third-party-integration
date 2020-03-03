package dtu.openhealth.integration.model

data class User(
        val userId: String,
        val token: String,
        val refreshToken: String
)