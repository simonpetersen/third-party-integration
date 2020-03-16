package dtu.openhealth.integration.controller

import dtu.openhealth.integration.model.User
import dtu.openhealth.integration.service.UserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
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
