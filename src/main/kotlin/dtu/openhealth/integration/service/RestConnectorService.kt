package dtu.openhealth.integration.service

import dtu.openhealth.integration.model.ThirdPartyNotification

interface RestConnectorService {
    fun retrieveDataForUser(notification: ThirdPartyNotification)
}