package dtu.openhealth.integration.service

import dtu.openhealth.integration.mapping.ThirdPartyMapper
import dtu.openhealth.integration.model.User

abstract class ThirdPartyPullService(
        private val httpService: HttpService) {

    fun pullData() {
        val users = getUserList()
        users.forEach { callEndpointsForUser(it) }
    }

    private fun callEndpointsForUser(user: User) {
        val urlParameters = getUserParameters(user)
        val thirdPartyData = httpService.callApiForUser(user, urlParameters)

        // TODO: Put result on Kafka stream.
        thirdPartyData.subscribe({ result -> println("Result = $result") }, { error -> println(error) })
    }

    abstract fun getUserList(): List<User>

    abstract fun getUserParameters(user: User): Map<String, String>
}