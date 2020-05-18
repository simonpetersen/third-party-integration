package dtu.openhealth.integration.shared.service.data

import dtu.openhealth.integration.shared.util.ConfigVault
import io.vertx.core.Future
import io.vertx.core.Promise
import io.vertx.kotlin.pgclient.pgConnectOptionsOf
import io.vertx.kotlin.sqlclient.poolOptionsOf
import io.vertx.pgclient.PgConnectOptions
import io.vertx.pgclient.PgPool
import io.vertx.reactivex.core.Vertx
import io.vertx.sqlclient.Row
import io.vertx.sqlclient.RowSet
import org.slf4j.LoggerFactory

abstract class BaseDataService(vertx: Vertx) {
    //private val configuration = PropertiesLoader.loadProperties()
    private val logger = LoggerFactory.getLogger(BaseDataService::class.java)

    private var connectOptions : PgConnectOptions? = null
    private var poolOptions = poolOptionsOf(maxSize = 5)
    private var client : PgPool? = null

    init {
        ConfigVault().getConfigRetriever(vertx).getConfig { ar ->
            if(ar.succeeded()){
                logger.info("Configuration retrieved from the vault")
                val config = ar.result()
                connectOptions = pgConnectOptionsOf(
                    database = config.getString("postgres.database"),
                    host = config.getString("postgres.host"),
                    password = config.getString("postgres.password"),
                    port = config.getString("postgres.port").toInt(),
                    user = config.getString("postgres.user")
                )
                client = PgPool.pool(vertx.delegate, connectOptions, poolOptions)
            }else{
                logger.error("${ar.cause()}")
            }
        }
    }

    protected fun executeQuery(query : String) : Future<RowSet<Row>> {
        val promise = Promise.promise<RowSet<Row>>()
        client?.query(query)?.execute { ar ->
            if (ar.succeeded()) {
                promise.complete(ar.result())
            } else {
                promise.fail(ar.cause())
            }
        }

        return promise.future()
    }
}
