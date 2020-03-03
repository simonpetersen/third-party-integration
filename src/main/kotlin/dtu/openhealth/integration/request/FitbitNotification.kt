package dtu.openhealth.integration.request

data class FitbitNotification(
        val ownerType: String,
        val ownerId: String,
        val collectionType: String,
        val date: String
)