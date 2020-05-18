package dtu.openhealth.integration.shared.service.tokenrefresh

import dtu.openhealth.integration.shared.model.UserToken

interface ITokenRefreshService {
    suspend fun refreshToken(userToken: UserToken): UserToken
}