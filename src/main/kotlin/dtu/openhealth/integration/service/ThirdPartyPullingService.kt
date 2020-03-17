package dtu.openhealth.integration.service

import dtu.openhealth.integration.mapping.ThirdPartyMapper
import dtu.openhealth.integration.model.User

abstract class ThirdPartyPullingService(
        private val mapper: ThirdPartyMapper,
        private val httpService: HttpService) {

    fun pullData() {
        val users = getUserList()
        users.forEach { callEndpointsForUser(it) }
    }

    private fun callEndpointsForUser(user: User) {
        val urlParameters = getUserParameters(user)
        val thirdPartyData = httpService.callApiForUser(user, urlParameters)
        for (data in thirdPartyData) {
            val omhData = mapper.mapData(data)
            println(omhData) // TODO: Put on Kafka stream.
        }
    }

    abstract fun getUserList(): List<User>

    abstract fun getUserParameters(user: User): Map<String, String>
}