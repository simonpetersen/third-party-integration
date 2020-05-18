package dtu.openhealth.integration.shared.util

import io.vertx.config.ConfigRetrieverOptions
import io.vertx.config.ConfigStoreOptions
import io.vertx.core.json.JsonObject
import io.vertx.core.logging.LoggerFactory
import io.vertx.reactivex.config.ConfigRetriever
import io.vertx.reactivex.core.Vertx

class ConfigVault {

    private val logger = LoggerFactory.getLogger(ConfigVault::class.java)

    fun getConfigRetriever(vertx: Vertx) : ConfigRetriever {
        val configuration = getConfig()

        val store = ConfigStoreOptions()
                .setType("vault")
                .setConfig(configuration)

        return ConfigRetriever.create(vertx,
                ConfigRetrieverOptions().addStore(store))
    }

    fun loadConfig(vertx: Vertx) {
        val configuration = getConfig()

        val store = ConfigStoreOptions()
                .setType("vault")
                .setConfig(configuration)

        val retriever = ConfigRetriever.create(vertx,
                ConfigRetrieverOptions().addStore(store))

        retriever.getConfig { ar ->
            if (ar.succeeded()) {
                logger.info("Configuration succussfully retrieved from the vault: ${ar.result()}")
            } else {
                logger.error(ar.cause())
            }
        }
    }

    private fun getConfig() : JsonObject {
        var vaultConfig: String = ""

        try {
            vaultConfig = this::class.java.classLoader.getResource("vault-config.json").readText()
        }catch (e: Exception) {
            logger.error("Missing vault-config.json file in properties")
        }

        return JsonObject(vaultConfig)
    }

}
