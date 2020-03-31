package dtu.openhealth.integration.shared.service

import dtu.openhealth.integration.shared.model.ThirdPartyNotification

interface ThirdPartyNotificationService {
    fun getUpdatedData(notificationList: List<ThirdPartyNotification>)
}