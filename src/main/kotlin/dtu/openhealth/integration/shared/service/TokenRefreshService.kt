package dtu.openhealth.integration.shared.service

import dtu.openhealth.integration.shared.model.UserToken

interface TokenRefreshService {
    suspend fun refreshToken(userToken: UserToken): UserToken
}