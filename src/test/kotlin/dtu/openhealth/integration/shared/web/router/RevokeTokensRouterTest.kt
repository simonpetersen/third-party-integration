package dtu.openhealth.integration.shared.web.router

import com.nhaarman.mockitokotlin2.*
import dtu.openhealth.integration.shared.model.UserToken
import dtu.openhealth.integration.shared.service.token.revoke.ITokenRevokeService
import dtu.openhealth.integration.shared.service.token.revoke.data.RevokeResponse
import io.reactivex.Single
import io.vertx.junit5.VertxExtension
import io.vertx.junit5.VertxTestContext
import io.vertx.reactivex.core.Vertx
import io.vertx.reactivex.core.buffer.Buffer
import io.vertx.reactivex.ext.web.client.HttpResponse
import io.vertx.reactivex.ext.web.client.WebClient
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(VertxExtension::class)
class RevokeTokensRouterTest {

    private val port = 8954
    private val thirdParty = "THIRDPARTY"
    private val userId1 = "jklsdajfls"
    private val userToken1 = UserToken(userId1, "ext1", thirdParty, "token1")
    private val userId2 = "jklfsadfsasdajfls"
    private val userToken2 = UserToken(userId2, "ext2", thirdParty, "token2")
    private val userTokenOtherThirdParty = UserToken("id3", "ext3", thirdParty, "token3")
    private val userIdJsonList ="""
    [
	    "$userId1",
	    "$userId2"
    ]"""

    @Test
    fun testCancelStudyEndpoint(vertx: Vertx, testContext: VertxTestContext)
    {
        val userList = mutableListOf(userToken1, userToken2, userTokenOtherThirdParty)
        val acceptedStatusCodes = listOf(200)
        val userTokenDataService = MockUserTokenDataService(userList)
        val tokenRevokeService : ITokenRevokeService = mock()
        val serviceMap = mapOf(Pair(thirdParty, tokenRevokeService))
        whenever(tokenRevokeService.revokeToken(userToken1)).thenReturn(singleRevokeResponse(userId1, 200))
        whenever(tokenRevokeService.revokeToken(userToken2)).thenReturn(singleRevokeResponse(userId2, 401))

        val cancelStudyRouter = RevokeTokensRouter(vertx, userTokenDataService, acceptedStatusCodes, serviceMap)
        vertx.createHttpServer()
                .requestHandler(cancelStudyRouter.getRouter())
                .listen(port, testContext.succeeding {
                    callCancelStudyEndpoint(vertx, testContext, userTokenDataService, tokenRevokeService)
        })
    }

    private fun callCancelStudyEndpoint(vertx: Vertx,
                                        testContext: VertxTestContext,
                                        userTokenDataService: MockUserTokenDataService,
                                        tokenRevokeService: ITokenRevokeService)
    {
        val webClient = WebClient.create(vertx)
        webClient.post(port, "localhost", "/study")
                .rxSendBuffer(Buffer.buffer(userIdJsonList))
                .subscribe(
                        { postSuccessful(it, testContext, userTokenDataService, tokenRevokeService) },
                        { testContext.failNow(it) }
                )
    }

    private fun postSuccessful(result: HttpResponse<Buffer>,
                               testContext: VertxTestContext,
                               userTokenDataService: MockUserTokenDataService,
                               tokenRevokeService: ITokenRevokeService)
    {
        testContext.verify {
            assertThat(result.statusCode()).isEqualTo(200)
            verify(tokenRevokeService).revokeToken(eq(userToken1))
            verify(tokenRevokeService).revokeToken(eq(userToken2))
            val userTokenList = userTokenDataService.userTokens()
            assertThat(userTokenList.size).isEqualTo(2)
        }
        testContext.completeNow()
    }

    private fun singleRevokeResponse(userId: String, statusCode: Int): Single<RevokeResponse>
    {
        val revokeResponse = RevokeResponse(userId, statusCode, "Unauthorized")
        return Single.just(revokeResponse)
    }
}
