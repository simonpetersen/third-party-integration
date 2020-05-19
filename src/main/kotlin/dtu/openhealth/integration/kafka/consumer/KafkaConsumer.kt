package dtu.openhealth.integration.kafka.consumer

import dtu.openhealth.integration.shared.dto.OmhDTO
import dtu.openhealth.integration.shared.service.omh.OmhService
import dtu.openhealth.integration.shared.util.ConfigVault
import io.vertx.core.logging.LoggerFactory
import io.vertx.reactivex.core.Vertx
import io.vertx.reactivex.kafka.client.consumer.KafkaConsumer
import java.util.HashMap

class KafkaConsumer(private val omhService: OmhService) {

    private val logger = LoggerFactory.getLogger(KafkaConsumer::class.java)

    private var topic = ""
    private var consumer: KafkaConsumer<String, OmhDTO>? = null

    fun startConsumer(vertx: Vertx) {
        ConfigVault().getConfigRetriever(vertx).getConfig { ar ->
            if(ar.succeeded()){
                logger.info("Configuration retrieved from the vault")
                val config = ar.result()
                val kafkaConfig: MutableMap<String, String> = HashMap()
                kafkaConfig["bootstrap.servers"] = config.getString("kafka.bootstrap.servers")
                kafkaConfig["key.deserializer"] = config.getString("kafka.string.deserializer")
                kafkaConfig["value.deserializer"] = config.getString("kafka.omh.deserializer")
                kafkaConfig["group.id"] = config.getString("kafka.group.id")
                kafkaConfig["auto.offset.reset"] = config.getString("kafka.auto.offset.reset")
                kafkaConfig["enable.auto.commit"] = config.getString("kafka.enable.auto.commit")
                topic = config.getString("kafka.topic")
                consumer = KafkaConsumer.create(vertx, kafkaConfig)
                consume()
            }else{
                logger.error(ar.cause())
            }
        }
    }

    private fun consume() {
        consumer?.handler { record ->
            logger.info("Getting data from Kafka stream $record")
            omhService.saveNewestOmhData(record.value())
        }

        consumer?.subscribe(topic) { ar ->
            if (ar.succeeded()) {
                logger.info("Kafka consumer subscribed to topic: $topic")
            } else {
                logger.error(ar.cause())
            }
        }
    }
    
}
