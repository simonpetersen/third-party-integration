package dtu.openhealth.integration.kafka.consumer

import dtu.openhealth.integration.shared.dto.OmhDTO
import dtu.openhealth.integration.shared.util.PropertiesLoader
import dtu.openhealth.integration.shared.service.omh.OmhService
import io.vertx.core.AsyncResult
import io.vertx.core.logging.LoggerFactory
import io.vertx.reactivex.core.Vertx
import io.vertx.reactivex.kafka.client.consumer.KafkaConsumer
import io.vertx.reactivex.kafka.client.consumer.KafkaConsumerRecord
import java.util.HashMap

class KafkaConsumer(
        vertx: Vertx,
        private val omhService: OmhService
) {

    private val logger = LoggerFactory.getLogger(KafkaConsumer::class.java)
    private val configuration = PropertiesLoader.loadProperties()
    
    private var consumer: KafkaConsumer<String, OmhDTO>
    init {
        val config: MutableMap<String, String> = HashMap()
        config["bootstrap.servers"] = configuration.getProperty("kafka.bootstrap.servers")
        config["key.deserializer"] = configuration.getProperty("kafka.string.deserializer")
        config["value.deserializer"] = configuration.getProperty("kafka.omh.deserializer")
        config["group.id"] = configuration.getProperty("kafka.group.id")
        config["auto.offset.reset"] = configuration.getProperty("kafka.auto.offset.reset")
        config["enable.auto.commit"] = configuration.getProperty("kafka_enable_auto_commit")
        consumer = KafkaConsumer.create(vertx, config)
    }

    fun consume()
    {
        val topic = configuration.getProperty("kafka.topic")
        consumer.handler { handleRecord(it) }
        consumer.subscribe(topic) { topicSubscribeHandler(it, topic) }
    }

    private fun handleRecord(record: KafkaConsumerRecord<String,OmhDTO>)
    {
        logger.info("Getting data from Kafka stream $record")
        omhService.saveNewestOmhData(record.value())
    }

    private fun topicSubscribeHandler(ar: AsyncResult<Void>, topic: String)
    {
        if (ar.succeeded()) {
            logger.info("Successfully subscribed to $topic")
        } else {
            val errorMsg = "Error occurred when subscribing to $topic"
            logger.error(errorMsg, ar.cause())
        }
    }
}
