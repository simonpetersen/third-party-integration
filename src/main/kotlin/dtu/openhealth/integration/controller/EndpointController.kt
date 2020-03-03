package dtu.openhealth.integration.controller

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class EndpointController {

    @PostMapping("/notification")
    fun notify(@RequestBody notification: String) : ResponseEntity<String> {
        // Receive data update notification
        // TODO: Retrieve new data
        return ResponseEntity(HttpStatus.OK)
    }
}