package dtu.openhealth.integration.shared.service

import dtu.openhealth.integration.shared.model.User

interface UserService {
    fun getUser(id: String): User?
    fun createUser(user: User)
    fun updateTokens(user: User)
    fun deleteUser(user: User)
    fun getNewestData(id: String): String
    fun addNewData(data: String)
    fun getAllUsers() : List<User>
}
