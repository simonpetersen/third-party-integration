package dtu.openhealth.integration

import dtu.openhealth.integration.model.User
import dtu.openhealth.integration.service.UserService
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit.jupiter.SpringExtension

@SpringBootTest
@ExtendWith(SpringExtension::class)
class UserServiceTest(@Autowired val userService: UserService) {

    @Test
    fun createUserTest() {
        val newUser: User = User("121","123","321")
        userService.createUser(newUser)
        val foundUser = userService.getUser("121")
        assertThat(newUser.userId).isEqualTo(foundUser.userId)
        assertThat(newUser.token).isEqualTo(foundUser.token)
        assertThat(newUser.refreshToken).isEqualTo(foundUser.refreshToken)
    }


}
