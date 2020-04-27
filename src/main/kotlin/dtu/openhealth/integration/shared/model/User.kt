package dtu.openhealth.integration.shared.model

import java.time.LocalDateTime

data class User(
        val userId: String,
        val extUserId: String,
        val token: String,
        val refreshToken: String? = null,
        val expireDateTime: LocalDateTime? = null,
        val tokenSecret: String? = null
)
