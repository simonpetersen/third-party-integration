package dtu.openhealth.integration.service

interface RestConnectorService {
    fun retrieveDataForUser(userId: String, collectionType: String, date: String)
}