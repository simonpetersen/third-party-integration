package dtu.openhealth.integration.fitbit.service.pull

import dtu.openhealth.integration.fitbit.data.FitbitConstants
import dtu.openhealth.integration.kafka.producer.IKafkaProducerService
import dtu.openhealth.integration.shared.model.RestEndpoint
import dtu.openhealth.integration.shared.model.UserToken
import dtu.openhealth.integration.shared.service.http.IHttpService
import dtu.openhealth.integration.shared.service.pull.AThirdPartyPullService
import dtu.openhealth.integration.shared.service.data.usertoken.IUserTokenDataService
import dtu.openhealth.integration.shared.service.token.refresh.ITokenRefreshService
import java.time.LocalDate

class FitbitPullService(
        httpService: IHttpService,
        endpointList: List<RestEndpoint>,
        kafkaProducerService: IKafkaProducerService,
        tokenRefreshService: ITokenRefreshService,
        private val userTokenDataService: IUserTokenDataService
): AThirdPartyPullService(httpService, kafkaProducerService, endpointList, tokenRefreshService) {

    override suspend fun getUserList(): List<UserToken>
    {
        return userTokenDataService.getTokensFromThirdParty(FitbitConstants.Fitbit)
    }

    override fun prepareUserParameterList(userToken: UserToken): List<Map<String, String>>
    {
        val date = LocalDate.now()
        val parameterList = mutableListOf<Map<String,String>>()

        for (i in 1..6) {
            parameterList.add(parametersAtDate(userToken, date))
            date.minusDays(1)
        }

        return parameterList
    }

    private fun parametersAtDate(userToken: UserToken, date: LocalDate): Map<String,String>
    {
        return mapOf(
                Pair("userId", userToken.extUserId),
                Pair("date", date.toString())
        )
    }
}