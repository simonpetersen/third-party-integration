package dtu.openhealth.integration.shared.service.pull

import io.vertx.kotlin.coroutines.dispatcher
import io.vertx.reactivex.core.Vertx
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.*

class PullTimerTask(
        private val vertx: Vertx,
        private val pullService: AThirdPartyPullService
): TimerTask() {

    override fun run() {
        GlobalScope.launch(vertx.delegate.dispatcher())
        {
            pullService.pullData()
        }
    }
}