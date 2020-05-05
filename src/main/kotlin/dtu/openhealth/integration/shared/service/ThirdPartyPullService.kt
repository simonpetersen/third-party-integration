package dtu.openhealth.integration.shared.service

import dtu.openhealth.integration.shared.model.RestEndpoint
import dtu.openhealth.integration.shared.model.UserToken

abstract class ThirdPartyPullService(
        private val httpService: HttpService,
        private val endpointList: List<RestEndpoint>) {

    fun pullData() {
        val users = getUserList()
        users.forEach { callEndpointsForUser(it) }
    }

    private fun callEndpointsForUser(userToken: UserToken) {
        val urlParameters = getUserParameters(userToken)
        val thirdPartyData = httpService.callApiForUser(endpointList, userToken, urlParameters)

        // TODO: Put result on Kafka stream.
        thirdPartyData.subscribe({ result -> println("Result = $result") }, { error -> println(error) })
    }

    abstract fun getUserList(): List<UserToken>

    abstract fun getUserParameters(userToken: UserToken): Map<String, String>
}