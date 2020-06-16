package dtu.openhealth.integration.shared.web.router

import dtu.openhealth.integration.shared.model.UserToken
import dtu.openhealth.integration.shared.service.data.usertoken.IUserTokenDataService
import dtu.openhealth.integration.shared.service.token.revoke.ITokenRevokeService
import dtu.openhealth.integration.shared.service.token.revoke.data.RevokeResponse
import io.reactivex.Single
import io.vertx.core.logging.LoggerFactory
import io.vertx.reactivex.ext.web.Router
import io.vertx.reactivex.core.Vertx
import io.vertx.reactivex.ext.web.RoutingContext
import io.vertx.reactivex.ext.web.handler.BodyHandler

class RevokeTokensRouter(
        private val vertx: Vertx,
        private val userTokenDataService: IUserTokenDataService,
        private val acceptedStatusCodes: List<Int>,
        private val revokeServiceMap: Map<String, ITokenRevokeService>
) {

    private val logger = LoggerFactory.getLogger(RevokeTokensRouter::class.java)

    fun getRouter(): Router
    {
        val router = Router.router(vertx)
        router.route().handler(BodyHandler.create())
        router.delete("/study").handler { handleDeleteUserList(it) }
        return router
    }

    private fun handleDeleteUserList(routingContext: RoutingContext)
    {
        val userIdList = routingContext.bodyAsJsonArray
                .toList()
                .filterIsInstance<String>()

        userTokenDataService.getTokensFromIdList(userIdList) {
            revokeTokensForUsers(it, routingContext)
        }
    }

    private fun revokeTokensForUsers(userTokens: List<UserToken>, routingContext: RoutingContext)
    {
        val responseList = userTokens.mapNotNull { revokeToken(it) }
        val responseSingle = Single.zip(responseList) {
            it.filterIsInstance<RevokeResponse>()
        }

        responseSingle.subscribe(
                { result -> handleRevokeResponseList(result, routingContext) },
                { error ->
                    val errorMsg = "Error when revoking tokens"
                    logger.error(errorMsg, error)
                }
        )
    }

    private fun revokeToken(userToken: UserToken): Single<RevokeResponse>?
    {
        val tokenRevokeService = revokeServiceMap[userToken.thirdParty]
        if (tokenRevokeService == null)
        {
            val errorMsg = "No TokenRevokeService has been set up for third party ${userToken.thirdParty}"
            logger.error(errorMsg)
            return null
        }

        return tokenRevokeService.revokeToken(userToken)
    }

    private fun handleRevokeResponseList(responseList: List<RevokeResponse>, routingContext: RoutingContext)
    {
        val failedRevocations = responseList
                .filter { !acceptedStatusCodes.contains(it.statusCode) }
        failedRevocations.forEach {
            val errorMsg = "Revoking token failed for ${it.userId}. Status Code = ${it.statusCode}. Response = ${it.responseBody}"
            logger.error(errorMsg)
        }

        val successfulRevocationIds = responseList.filter {
            rev -> failedRevocations.none { failedRev -> failedRev.userId == rev.userId }
        }
                .map { it.userId }
        logger.info("Deleting tokens: $successfulRevocationIds")
        userTokenDataService.deleteTokensInList(successfulRevocationIds)
        routingContext.response().end()
    }
}
