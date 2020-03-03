package dtu.openhealth.integration.service.impl

import dtu.openhealth.integration.data.FitbitData
import dtu.openhealth.integration.model.RestEndpoint
import dtu.openhealth.integration.model.User
import dtu.openhealth.integration.service.RestConnectorService
import dtu.openhealth.integration.web.RestConnector
import org.springframework.stereotype.Service

@Service
class RestConnectorServiceImpl : RestConnectorService {
    private val urlMap = HashMap<String, List<RestEndpoint>>()
    private val userTokens = HashMap<String, User>()
    private val restConnector = RestConnector()

    init {
        val endpoints = listOf(RestEndpoint("https://api.fitbit.com/1/user/[userId]/activities/calories/date/[date]/1d.json", FitbitData.serializer()))
        urlMap.put("activities", endpoints)
        userTokens["89NGPS"] = User("89NGPS", "eyJhbGciOiJIUzI1NiJ9.eyJhdWQiOiIyMkI5QlciLCJzdWIiOiI4OU5HUFMiLCJpc3MiOiJGaXRiaXQiLCJ0eXAiOiJhY2Nlc3NfdG9rZW4iLCJzY29wZXMiOiJ3aHIgd3BybyB3bnV0IHdzbGUgd3dlaSB3c29jIHdzZXQgd2FjdCIsImV4cCI6MTU4MzI1MzcxMywiaWF0IjoxNTgzMjI0OTEzfQ.IkTNFiMR6WjpbA4HmJor0gqMLfP7a9X6chHMwg2i3Hk", "50e641f0c2f4611a2958ac0b669dac4220e1a85f0c8a5a045997e2fac5197df9")
    }

    override fun retrieveDataForUser(userId: String, collectionType: String, date: String) {
        println("RetrieveDataForUser called.")
        val user = userTokens[userId]
        if (user != null) {
            val endpoints = urlMap[collectionType] ?: emptyList()
            for (endpoint in endpoints) {
                var modUrl = endpoint.url.replace("[userId]", userId)
                modUrl = modUrl.replace("[date]", date)
                restConnector.get(modUrl, endpoint.serializer, user.token)
            }
        }
    }
}