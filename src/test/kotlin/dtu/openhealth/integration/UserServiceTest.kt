package dtu.openhealth.integration

import dtu.openhealth.integration.shared.model.User
import dtu.openhealth.integration.shared.service.UserService
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit.jupiter.SpringExtension

@SpringBootTest
@ExtendWith(SpringExtension::class)
class UserServiceTest(@Autowired val userService: UserService) {

    @Test
    fun createUserTest() {
        val newUser = User("121","123","321")
        userService.createUser(newUser)
        val foundUser = userService.getUser("121")
        if(foundUser != null) {
            assertThat(newUser.userId).isEqualTo(foundUser.userId)
            assertThat(newUser.token).isEqualTo(foundUser.token)
            assertThat(newUser.refreshToken).isEqualTo(foundUser.refreshToken)
        }
    }

    @Test
    fun updateUserTokensTest() {
        val newUser = User("121","123","321")
        userService.createUser(newUser)
        userService.updateTokens(User("121","321","123"))
        val foundUser = userService.getUser("121")
        if(foundUser != null){
            assertThat("321").isEqualTo(foundUser.token)
            assertThat("123").isEqualTo(foundUser.refreshToken)
        }
    }

    @Test
    fun deleteUserTest() {
        val newUser = User("121","123","321")
        userService.createUser(newUser)
        assertThat(userService.getUser("121")).isNotNull
        userService.deleteUser(newUser)
        assertThat(userService.getUser("121")).isNull()
    }


}
