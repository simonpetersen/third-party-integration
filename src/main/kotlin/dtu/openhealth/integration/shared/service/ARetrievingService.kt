package dtu.openhealth.integration.shared.service

import dtu.openhealth.integration.kafka.producer.IKafkaProducerService
import dtu.openhealth.integration.shared.model.RestEndpoint
import dtu.openhealth.integration.shared.model.AThirdPartyData
import dtu.openhealth.integration.shared.model.UserToken
import dtu.openhealth.integration.shared.service.http.IHttpService
import io.vertx.core.logging.LoggerFactory
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import java.time.LocalDateTime

abstract class ARetrievingService(
        private val httpService: IHttpService,
        private val kafkaProducerService: IKafkaProducerService
) {

    private val logger = LoggerFactory.getLogger(ARetrievingService::class.java)
    private val json = Json(JsonConfiguration.Stable)

    protected fun callApiAndPublishOmhData(userToken: UserToken, endpointList: List<RestEndpoint>, parameters: Map<String,String>)
    {
        val responseList = httpService.callApiForUser(endpointList, userToken, parameters)

        responseList.subscribe(
                { result ->
                    result.mapNotNull {
                        convertJsonToThirdPartyData(it.responseJson, it.serializer)
                                ?.mapToOMH(it.parameters)
                    }
                            .forEach { kafkaProducerService.sendOmhData(it) }
                },
                { error ->
                    val errorMsg = "Error getting data for $userToken. Parameters = $parameters"
                    logger.error(errorMsg, error)
                }
        )
    }

    private fun convertJsonToThirdPartyData(responseJson: String, serializer: KSerializer<out AThirdPartyData>) : AThirdPartyData?
    {
        return try {
            json.parse(serializer, responseJson)
        } catch (e: Exception) {
            logger.error(responseJson)
            logger.error(e)
            null
        }
    }

    protected fun tokenIsExpired(expireDateTime: LocalDateTime?): Boolean
    {
        if (expireDateTime == null) {
            return false
        }

        val now = LocalDateTime.now().minusSeconds(5)
        return expireDateTime.isBefore(now)
    }
}