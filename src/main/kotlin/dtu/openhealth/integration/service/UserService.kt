package dtu.openhealth.integration.service

import dtu.openhealth.integration.data.omh.OpenMHealthData
import dtu.openhealth.integration.model.User
import org.springframework.stereotype.Service

interface UserService {
    fun getUser(id: String): User
    fun createUser(user: User)
    fun updateTokens(user: User)
    fun deleteUser(user: User)
    fun getNewestData(id: String): OpenMHealthData
    fun addNewData(data: OpenMHealthData?)
}
