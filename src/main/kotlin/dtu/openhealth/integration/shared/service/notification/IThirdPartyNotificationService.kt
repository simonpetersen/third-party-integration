package dtu.openhealth.integration.shared.service.notification

import dtu.openhealth.integration.shared.model.ThirdPartyNotification

interface IThirdPartyNotificationService {
    suspend fun getUpdatedData(notificationList: List<ThirdPartyNotification>)
}