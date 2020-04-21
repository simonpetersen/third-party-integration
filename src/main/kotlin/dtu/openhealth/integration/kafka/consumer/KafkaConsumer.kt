package dtu.openhealth.integration.kafka.consumer

import dtu.openhealth.integration.kafka.consumer.property.KafkaConsumerProperties
import dtu.openhealth.integration.shared.dto.OmhDTO
import io.vertx.core.logging.LoggerFactory
import io.vertx.reactivex.core.Vertx
import io.vertx.reactivex.kafka.client.consumer.KafkaConsumer
import io.vertx.reactivex.kafka.client.consumer.KafkaConsumerRecord
import java.util.HashMap

class KafkaConsumer(vertx: Vertx) {

    private val LOGGER = LoggerFactory.getLogger(KafkaConsumer::class.java)
    
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
            LOGGER.info("Getting data from Kafka stream $record")
            consumeOmhData(record.value())
        }
        
        consumer.subscribe(KafkaConsumerProperties.TOPIC) { ar ->
            if (ar.succeeded()) {
                LOGGER.info("subscribed")
            } else {
                LOGGER.info("Could not subscribe ${ar.cause().message}")
            }
        }
    }

    private fun consumeOmhData(omhDTO: OmhDTO) {
        LOGGER.info("Consume OmhDTO: $omhDTO")
    }
}
