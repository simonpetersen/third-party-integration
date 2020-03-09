package dtu.openhealth.integration.service.impl

import dtu.openhealth.integration.data.omh.OpenMHealthData
import dtu.openhealth.integration.model.User
import dtu.openhealth.integration.model.repository.UserRepository
import dtu.openhealth.integration.service.UserService
import org.apache.logging.log4j.LogManager
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class UserServiceImpl(@Autowired private val userRepository: UserRepository) : UserService {

    companion object {
        private val logger = LogManager.getLogger()
    }

    override fun getUser(id: String): User {
        logger.info("Getting user with id: $id")
        return userRepository.findById(id)
                .orElseThrow()
    }

    override fun createUser(user: User) {
        logger.info("Saving user: ${user.userId}, ${user.refreshToken}, ${user.token}")
        userRepository.save(user)
    }

    override fun updateTokens(user: User) {
        logger.info("Updating tokens for user with id: ${user.userId} with values, " +
                "refresh: ${user.refreshToken} and token: ${user.token}")
        userRepository.updateTokens(user.token, user.refreshToken, user.userId)
    }

    override fun deleteUser(user: User) {
        logger.info("Deleting user with id: ${user.userId}")
        userRepository.delete(user)
    }

    override fun getNewestData(id: String): OpenMHealthData {
        //TODO
        return OpenMHealthData()
    }

    override fun addNewData(data: OpenMHealthData?) {
        //TODO
    }

}
