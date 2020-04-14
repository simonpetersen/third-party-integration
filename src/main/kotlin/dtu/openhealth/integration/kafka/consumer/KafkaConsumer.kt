package dtu.openhealth.integration.kafka.consumer

import dtu.openhealth.integration.garmin.garmin.ActivitySummaryGarmin
import dtu.openhealth.integration.kafka.consumer.property.KafkaConsumerProperties
import dtu.openhealth.integration.kafka.publisher.impl.KafkaProducerServiceImpl
import dtu.openhealth.integration.shared.dto.OmhDTO
import io.vertx.core.buffer.Buffer
import io.vertx.core.json.JsonObject
import io.vertx.core.logging.LoggerFactory
import io.vertx.core.parsetools.JsonParser
import io.vertx.reactivex.core.Vertx
import io.vertx.reactivex.kafka.client.consumer.KafkaConsumer
import io.vertx.reactivex.kafka.client.consumer.KafkaConsumerRecord
import kotlinx.serialization.json.Json
import kotlinx.serialization.parseMap
import java.util.HashMap

class KafkaConsumer(vertx: Vertx) {

    private val LOGGER = LoggerFactory.getLogger(KafkaConsumer::class.java)
    
    private var consumer: KafkaConsumer<Any, Any>
    init {
        val config: MutableMap<String, String> = HashMap()
        config["bootstrap.servers"] = KafkaConsumerProperties.BOOTSTRAP_SERVERS
        config["key.deserializer"] = KafkaConsumerProperties.STRING_DESERIALIZER
        config["value.deserializer"] = KafkaConsumerProperties.OMH_DESERIALIZER
        config["group.id"] = KafkaConsumerProperties.GROUP_ID
        config["auto.offset.reset"] = KafkaConsumerProperties.AUTO_OFFSET_RESET
        config["enable.auto.commit"] = KafkaConsumerProperties.ENABLE_AUTO_COMMIT
        consumer = KafkaConsumer.create<Any, Any>(vertx, config)
        
        consumer.handler { record ->
            LOGGER.info("Getting data from Kafka stream $record")
            consumeOmhData(getOmhData(record))
        }
        
        consumer.subscribe(KafkaConsumerProperties.TOPIC) { ar ->
            if (ar.succeeded()) {
                LOGGER.info("subscribed")
            } else {
                LOGGER.info("Could not subscribe ${ar.cause().message}")
            }
        }
    }
    
    private fun getOmhData(kafkaConsumerRecord: KafkaConsumerRecord<Any, Any>): OmhDTO {
        LOGGER.info("Key: ${kafkaConsumerRecord.key()} & Value: ${kafkaConsumerRecord.value()}")
        return kafkaConsumerRecord.value() as OmhDTO
    }
    
    private fun consumeOmhData(omhDTO: OmhDTO) {
        LOGGER.info(omhDTO)
    }
}
