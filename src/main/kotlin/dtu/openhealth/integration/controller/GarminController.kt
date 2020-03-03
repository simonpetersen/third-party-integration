package dtu.openhealth.integration.controller

import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import dtu.openhealth.integration.data.garmin.BodyCompositionSummaryGarmin
import dtu.openhealth.integration.data.garmin.DailySummaryGarmin
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.apache.logging.log4j.LogManager

@RestController
@RequestMapping(path = ["/api/garmin"])
class GarminController {

    companion object {
        private val logger = LogManager.getLogger()
    }

    //TODO: Have to add that this is forwarded to a class that handles transfering to OMH and then forward to Kafka Stream

    @PostMapping(path = ["/body"])
    fun bodyCompositionSummary(@RequestBody jsonObject: String): ResponseEntity<String> {
        logger.info("Received body data: $jsonObject")
        val obj: JsonObject = Gson().fromJson(jsonObject, JsonObject::class.java)
        val objArray: JsonArray = Gson().fromJson(obj.get("body").asJsonArray, JsonArray::class.java)
        for (jsonObj in objArray) println(Gson().fromJson(jsonObj, BodyCompositionSummaryGarmin::class.java))
        return ResponseEntity(HttpStatus.CREATED)
    }

    @PostMapping(path = ["/daily"])
    fun dailySummary(@RequestBody jsonObject: String): ResponseEntity<String> {
        logger.info("Received daily data: $jsonObject")
        val obj: JsonObject = Gson().fromJson(jsonObject, JsonObject::class.java)
        val objArray: JsonArray = Gson().fromJson(obj.get("dailies").asJsonArray, JsonArray::class.java)
        for (jsonObj in objArray) println(Gson().fromJson(jsonObj, DailySummaryGarmin::class.java))
        return ResponseEntity(HttpStatus.CREATED)
    }
}
