package dtu.openhealth.integration.shared.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
class AuthorizationController {

    // Controller handling authorization of participants.
    // Might not be needed.

    @GetMapping("/auth")
    fun redirect(@RequestParam("oauth_token") token: String, @RequestParam("oauth_verifier") verifier: String): String {
        println(token)
        println(verifier)
        return "12345"
    }

    // For testing purposes
    @GetMapping("/login")
    fun login(@RequestParam("code") authCode: String): String {
        println("OAuth code = $authCode")
        return "Login successful"
    }
}