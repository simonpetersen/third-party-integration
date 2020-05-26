package dtu.openhealth.integration.shared.service.token.revoke

import dtu.openhealth.integration.shared.model.UserToken
import dtu.openhealth.integration.shared.service.token.revoke.data.RevokeResponse
import io.reactivex.Single

interface ITokenRevokeService {
    fun revokeToken(userToken: UserToken): Single<RevokeResponse>
}