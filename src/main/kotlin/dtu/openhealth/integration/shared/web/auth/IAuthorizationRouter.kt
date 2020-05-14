package dtu.openhealth.integration.shared.web.auth

import io.vertx.reactivex.ext.web.Router

interface IAuthorizationRouter {
    fun getRouter(): Router
}