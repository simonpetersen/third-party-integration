package dtu.openhealth.integration.shared.verticle

import dtu.openhealth.integration.shared.service.impl.*
import dtu.openhealth.integration.shared.util.PropertiesLoader
import io.vertx.reactivex.core.AbstractVerticle

class  MainVerticle : AbstractVerticle() {

    private val configuration = PropertiesLoader.loadProperties()

    override fun start() {
        val userDataService = UserDataServiceImpl(vertx.delegate)
        vertx.deployVerticle(WebServerVerticle(userDataService))
    }

}
