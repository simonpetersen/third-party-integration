package dtu.openhealth.integration.controller

import dtu.openhealth.integration.request.FitbitNotification
import dtu.openhealth.integration.service.RestConnectorService
import dtu.openhealth.integration.service.impl.RestConnectorServiceImpl
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

// Controller providing endpoints for updates from third parties.
@RestController
class EndpointController(private val restConnectorService: RestConnectorService) {

    @PostMapping("/notification")
    fun notify(@RequestBody notification: FitbitNotification) : ResponseEntity<String> {
        if (notification.ownerType == "user") {
            restConnectorService.retrieveDataForUser(notification.ownerId, notification.collectionType, notification.date)
        }

        return ResponseEntity(HttpStatus.NO_CONTENT)
    }
}