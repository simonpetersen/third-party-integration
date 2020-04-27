package dtu.openhealth.integration.shared.web.auth

import io.vertx.reactivex.ext.web.Router

interface AuthenticationRouter {
    fun getRouter(): Router

}