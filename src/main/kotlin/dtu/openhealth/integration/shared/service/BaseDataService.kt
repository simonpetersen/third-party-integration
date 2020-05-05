package dtu.openhealth.integration.shared.service

import dtu.openhealth.integration.shared.util.PropertiesLoader
import io.vertx.core.Future
import io.vertx.core.Promise
import io.vertx.kotlin.pgclient.pgConnectOptionsOf
import io.vertx.kotlin.sqlclient.poolOptionsOf
import io.vertx.pgclient.PgPool
import io.vertx.core.Vertx
import io.vertx.core.json.JsonArray
import io.vertx.sqlclient.Row
import io.vertx.sqlclient.RowSet

abstract class BaseDataService(vertx: Vertx) {
    private val configuration = PropertiesLoader.loadProperties()

    private val connectOptions = pgConnectOptionsOf(
            database = configuration.getProperty("postgres.database"),
            host = configuration.getProperty("postgres.host"),
            password = configuration.getProperty("postgres.password"),
            port = configuration.getProperty("postgres.port").toInt(),
            user = configuration.getProperty("postgres.user"))

    private val poolOptions = poolOptionsOf(maxSize = 5)

    private val client : PgPool = PgPool.pool(vertx, connectOptions, poolOptions)

    protected fun executeQuery(query : String) : Future<RowSet<Row>> {
        val promise = Promise.promise<RowSet<Row>>()
        client.query(query).execute { ar ->
            if (ar.succeeded()) {
                promise.complete(ar.result())
            } else {
                promise.fail(ar.cause())
            }
        }

        return promise.future()
    }
}
