package dtu.openhealth.integration.service

import dtu.openhealth.integration.model.User

interface UserService {
    fun getUser(id: String): User?
    fun createUser(user: User)
    fun updateTokens(user: User)
    fun deleteUser(user: User)
    fun getNewestData(id: String): String
    fun addNewData(data: String)
    fun getAllUsers() : List<User>
}
