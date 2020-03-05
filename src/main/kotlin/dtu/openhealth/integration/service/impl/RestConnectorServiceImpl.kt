package dtu.openhealth.integration.service.impl

import dtu.openhealth.integration.data.FitbitActivitiesCalories
import dtu.openhealth.integration.data.FitbitData
import dtu.openhealth.integration.data.ThirdPartyData
import dtu.openhealth.integration.mapping.FitbitMapper
import dtu.openhealth.integration.model.RestEndpoint
import dtu.openhealth.integration.model.User
import dtu.openhealth.integration.service.RestConnectorService
import dtu.openhealth.integration.web.RestConnector
import org.springframework.stereotype.Service

@Service
class RestConnectorServiceImpl : RestConnectorService {

    private val urlMap = HashMap<String, RestEndpoint>()
    private val userTokens = HashMap<String, User>() // Temp mock DB with HashMaps

    private val restConnector = RestConnector()
    private val fitbitMapper = FitbitMapper() // Should be extracted to MappingService

    init {
        val endpoint = RestEndpoint(listOf("https://api.fitbit.com/1/user/[userId]/activities/calories/date/[date]/1d.json"), FitbitActivitiesCalories.serializer())
        urlMap["activities"] = endpoint
        userTokens["89NGPS"] = User("89NGPS", "eyJhbGciOiJIUzI1NiJ9.eyJhdWQiOiIyMkI5QlciLCJzdWIiOiI4OU5HUFMiLCJpc3MiOiJGaXRiaXQiLCJ0eXAiOiJhY2Nlc3NfdG9rZW4iLCJzY29wZXMiOiJ3aHIgd3BybyB3bnV0IHdzbGUgd3dlaSB3c29jIHdhY3Qgd3NldCIsImV4cCI6MTU4MzQyNzM2OSwiaWF0IjoxNTgzMzk4NTY5fQ.38uZ_1ChNLIzHxMN-7ul39v31C9c-AsjdOKQR-05B4g", "123")
    }

    override fun retrieveDataForUser(userId: String, collectionType: String, date: String) {
        val user = userTokens[userId]
        if (user != null) {
            val endpoint = urlMap[collectionType] ?: RestEndpoint(emptyList(), ThirdPartyData.serializer())
            for (url in endpoint.urls) {
                var modUrl = url.replace("[userId]", userId)
                modUrl = modUrl.replace("[date]", date)
                val thirdPartyData = restConnector.get(modUrl, endpoint.serializer, user.token)
                val omhData = fitbitMapper.mapData(thirdPartyData)
                println(omhData)
            }
        }
    }
}