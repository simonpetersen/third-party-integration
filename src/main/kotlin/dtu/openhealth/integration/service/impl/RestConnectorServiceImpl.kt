package dtu.openhealth.integration.service.impl

import dtu.openhealth.integration.data.FitbitData
import dtu.openhealth.integration.data.ThirdPartyData
import dtu.openhealth.integration.model.RestEndpoint
import dtu.openhealth.integration.model.User
import dtu.openhealth.integration.service.RestConnectorService
import dtu.openhealth.integration.web.RestConnector
import org.springframework.stereotype.Service

@Service
class RestConnectorServiceImpl : RestConnectorService {
    private val urlMap = HashMap<String, RestEndpoint>()
    private val userTokens = HashMap<String, User>()
    private val restConnector = RestConnector()

    init {
        val endpoint = RestEndpoint(listOf("https://api.fitbit.com/1/user/[userId]/activities/calories/date/[date]/1d.json"), FitbitData.serializer())
        urlMap["activities"] = endpoint
        userTokens["89NGPS"] = User("89NGPS", "123", "123")
    }

    override fun retrieveDataForUser(userId: String, collectionType: String, date: String) {
        val user = userTokens[userId]
        if (user != null) {
            val endpoint = urlMap[collectionType] ?: RestEndpoint(emptyList(), ThirdPartyData.serializer())
            for (url in endpoint.urls) {
                var modUrl = url.replace("[userId]", userId)
                modUrl = modUrl.replace("[date]", date)
                restConnector.get(modUrl, endpoint.serializer, user.token)
            }
        }
    }
}