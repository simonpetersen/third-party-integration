package dtu.openhealth.integration.shared.service

import dtu.openhealth.integration.shared.model.ThirdPartyNotification

interface ThirdPartyNotificationService {
    suspend fun getUpdatedData(notificationList: List<ThirdPartyNotification>)
}