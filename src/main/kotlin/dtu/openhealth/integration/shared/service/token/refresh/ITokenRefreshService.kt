package dtu.openhealth.integration.shared.service.token.refresh

import dtu.openhealth.integration.shared.model.UserToken

interface ITokenRefreshService {
    fun refreshToken(userToken: UserToken, callback: (UserToken) -> Unit)
}