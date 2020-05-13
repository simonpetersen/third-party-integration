package dtu.openhealth.integration.shared.verticle

import dtu.openhealth.integration.shared.model.ThirdPartyNotification
import dtu.openhealth.integration.shared.service.ThirdPartyNotificationService
import io.vertx.kotlin.coroutines.dispatcher
import io.vertx.reactivex.core.Vertx
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

open class BaseNotificationEndpointRouter(private val vertx: Vertx,
                                    private val notificationService: ThirdPartyNotificationService) {

    protected fun handleNotificationList(notificationList: List<ThirdPartyNotification>) {
        GlobalScope.launch(vertx.delegate.dispatcher()) {
            // Retrieve data in coroutine and reply immediately
            notificationService.getUpdatedData(notificationList)
        }
    }
}