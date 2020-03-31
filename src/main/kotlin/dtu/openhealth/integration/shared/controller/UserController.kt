package dtu.openhealth.integration.shared.controller

import dtu.openhealth.integration.shared.model.User
import dtu.openhealth.integration.shared.service.UserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping(path = ["/user"])
class UserController @Autowired constructor(private val userService: UserService) {

    @GetMapping(path = ["/{id}"])
    fun getUser(@PathVariable id: String): User? {
        return userService.getUser(id)
    }

    @PostMapping(path = ["add"])
    fun addUser(@RequestBody user: User) {
        userService.createUser(user)
    }

    @PutMapping(path = ["/tokens"])
    fun updateTokens(@RequestBody user: User) {
        userService.updateTokens(user)
    }

}
