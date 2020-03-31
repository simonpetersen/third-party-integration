package dtu.openhealth.integration.service.impl

import dtu.openhealth.integration.model.User
import dtu.openhealth.integration.service.UserService

class TestUserServiceImpl : UserService {
    private val token = "tokenABC"
    private val user = User("89NGPS", token, "123")

    override fun getUser(id: String): User? {
        return user
    }

    override fun createUser(user: User) {
        TODO("Not yet implemented")
    }

    override fun updateTokens(user: User) {
        TODO("Not yet implemented")
    }

    override fun deleteUser(user: User) {
        TODO("Not yet implemented")
    }

    override fun getNewestData(id: String): String {
        TODO("Not yet implemented")
    }

    override fun addNewData(data: String) {
        TODO("Not yet implemented")
    }

    override fun getAllUsers(): List<User> {
        return listOf(user)
    }
}