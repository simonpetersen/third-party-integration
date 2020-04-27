package dtu.openhealth.integration.shared.service.impl

import dtu.openhealth.integration.shared.model.User
import dtu.openhealth.integration.shared.service.UserService
import org.apache.logging.log4j.LogManager

class UserServiceImpl() : UserService {

    companion object {
        private val logger = LogManager.getLogger()
    }

    override fun getUser(id: String): User? {
        logger.info("Getting user with id: $id")
        return null
    }

    override fun createUser(user: User) {
        logger.info("Saving user: ${user.userId}, ${user.refreshToken}, ${user.token}")
    }

    override fun updateTokens(user: User) {
        logger.info("Updating tokens for user with id: ${user.userId} with values, " +
                "refresh: ${user.refreshToken} and token: ${user.token}")
    }

    override fun deleteUser(user: User) {
        logger.info("Deleting user with id: ${user.userId}")
    }

    override fun getNewestData(id: String): String {
        //TODO
        return ""
    }

    override fun addNewData(data: String) {
        //TODO
    }

    override fun getAllUsers(): List<User> {
        TODO("Not yet implemented")
    }

}
