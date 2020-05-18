package dtu.openhealth.integration.shared.web.router

import dtu.openhealth.integration.shared.model.ThirdPartyNotification
import dtu.openhealth.integration.shared.service.notification.IThirdPartyNotificationService
import io.vertx.kotlin.coroutines.dispatcher
import io.vertx.reactivex.core.Vertx
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

open class BaseNotificationEndpointRouter(
        private val vertx: Vertx,
        private val notificationService: IThirdPartyNotificationService
) {

    protected fun handleNotificationList(notificationList: List<ThirdPartyNotification>) {
        GlobalScope.launch(vertx.delegate.dispatcher()) {
            // Retrieve data in coroutine and reply immediately
            notificationService.getUpdatedData(notificationList)
        }
    }
}