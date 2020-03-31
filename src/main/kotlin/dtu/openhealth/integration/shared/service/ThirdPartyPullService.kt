package dtu.openhealth.integration.shared.service

import dtu.openhealth.integration.shared.model.RestEndpoint
import dtu.openhealth.integration.shared.model.User

abstract class ThirdPartyPullService(
        private val httpService: HttpService,
        private val endpointList: List<RestEndpoint>) {

    fun pullData() {
        val users = getUserList()
        users.forEach { callEndpointsForUser(it) }
    }

    private fun callEndpointsForUser(user: User) {
        val urlParameters = getUserParameters(user)
        val thirdPartyData = httpService.callApiForUser(endpointList, user, urlParameters)

        // TODO: Put result on Kafka stream.
        thirdPartyData.subscribe({ result -> println("Result = $result") }, { error -> println(error) })
    }

    abstract fun getUserList(): List<User>

    abstract fun getUserParameters(user: User): Map<String, String>
}