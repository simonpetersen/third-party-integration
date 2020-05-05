package dtu.openhealth.integration.kafka.consumer

import dtu.openhealth.integration.kafka.consumer.property.KafkaConsumerProperties
import dtu.openhealth.integration.shared.dto.OmhDTO
import dtu.openhealth.integration.shared.service.OmhService
import io.vertx.core.logging.LoggerFactory
import io.vertx.reactivex.core.Vertx
import io.vertx.reactivex.kafka.client.consumer.KafkaConsumer
import java.util.HashMap

class KafkaConsumer(vertx: Vertx, private val omhService: OmhService) {

    private val logger = LoggerFactory.getLogger(KafkaConsumer::class.java)
    
    private var consumer: KafkaConsumer<String, OmhDTO>
    init {
        val config: MutableMap<String, String> = HashMap()
        config["bootstrap.servers"] = KafkaConsumerProperties.BOOTSTRAP_SERVERS
        config["key.deserializer"] = KafkaConsumerProperties.STRING_DESERIALIZER
        config["value.deserializer"] = KafkaConsumerProperties.OMH_DESERIALIZER
        config["group.id"] = KafkaConsumerProperties.GROUP_ID
        config["auto.offset.reset"] = KafkaConsumerProperties.AUTO_OFFSET_RESET
        config["enable.auto.commit"] = KafkaConsumerProperties.ENABLE_AUTO_COMMIT
        consumer = KafkaConsumer.create(vertx, config)
    }

    fun consume() {
        consumer.handler { record ->
            logger.info("Getting data from Kafka stream $record")
            omhService.saveNewestOmhData(record.value())
        }
        
        consumer.subscribe(KafkaConsumerProperties.TOPIC) { ar ->
            if (ar.succeeded()) {
                logger.info("subscribed")
            } else {
                logger.error(ar.cause())
            }
        }
    }
}
