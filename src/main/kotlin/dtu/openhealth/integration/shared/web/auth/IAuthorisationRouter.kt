package dtu.openhealth.integration.shared.web.auth

import io.vertx.reactivex.ext.web.Router

interface IAuthorisationRouter {
    fun getRouter(): Router
}