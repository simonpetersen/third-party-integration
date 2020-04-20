package dtu.openhealth.integration.shared.service

import io.vertx.core.Future
import io.vertx.core.Promise
import io.vertx.kotlin.pgclient.pgConnectOptionsOf
import io.vertx.kotlin.sqlclient.poolOptionsOf
import io.vertx.pgclient.PgPool
import io.vertx.core.Vertx
import io.vertx.sqlclient.Row
import io.vertx.sqlclient.RowSet

abstract class BaseDataService(vertx: Vertx) {
    private val connectOptions = pgConnectOptionsOf(database = "integration-db",
            host = "localhost",
            password = "carp_integration", port = 5432,
            user = "postgres")

    private val poolOptions = poolOptionsOf(maxSize = 5)

    private val client : PgPool = PgPool.pool(vertx, connectOptions, poolOptions)

    protected fun executeQuery(sql : String) : Future<RowSet<Row>> {
        val promise = Promise.promise<RowSet<Row>>()
        client.query(sql).execute { ar ->
            if (ar.succeeded()) {
                promise.complete(ar.result())
            } else {
                promise.fail(ar.cause())
            }
        }

        return promise.future()
    }
}