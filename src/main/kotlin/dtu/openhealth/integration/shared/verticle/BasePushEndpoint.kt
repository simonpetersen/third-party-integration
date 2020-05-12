package dtu.openhealth.integration.shared.verticle

import dtu.openhealth.integration.shared.model.ThirdPartyData
import dtu.openhealth.integration.shared.service.ThirdPartyPushService
import io.vertx.core.json.JsonArray
import io.vertx.core.json.JsonObject
import io.vertx.core.logging.LoggerFactory
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration

open class BasePushEndpointRouter(private val pushService: ThirdPartyPushService) {

    private val logger = LoggerFactory.getLogger(BasePushEndpointRouter::class.java)
    private val json = Json(JsonConfiguration.Stable)

    protected fun convertArrayAndSaveData(jsonArray: JsonArray, serializer: DeserializationStrategy<out ThirdPartyData>) {
        jsonArray.forEach {
            data ->
            if(data is JsonObject) {
                try{
                    val parsedData = json.parse(serializer, data.toString())
                    logger.info("Saving data $parsedData to omh")
                    pushService.saveDataToOMH(parsedData)
                }catch (e: Exception){
                    logger.error(e)
                }
            }
        }
    }
}