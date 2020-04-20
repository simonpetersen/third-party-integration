package dtu.openhealth.integration.shared.service

import dtu.openhealth.integration.shared.model.User

interface TokenRefreshService {
    suspend fun refreshToken(user: User): User
}