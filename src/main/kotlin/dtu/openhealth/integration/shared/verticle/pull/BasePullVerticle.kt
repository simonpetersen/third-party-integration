package dtu.openhealth.integration.shared.verticle.pull

import dtu.openhealth.integration.shared.service.pull.AThirdPartyPullService
import dtu.openhealth.integration.shared.service.pull.PullTimerTask
import io.vertx.reactivex.core.AbstractVerticle
import java.util.*

open class BasePullVerticle : AbstractVerticle() {

    protected fun startTimer(pullService: AThirdPartyPullService, intervalMinutes: Int)
    {
        val timer = Timer()
        val interval = intervalMinutes * 60 * 1000L
        val pullTimerTask = PullTimerTask(vertx, pullService)
        timer.scheduleAtFixedRate(pullTimerTask, 0, interval)
    }
}