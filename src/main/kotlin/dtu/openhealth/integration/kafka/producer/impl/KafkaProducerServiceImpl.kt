package dtu.openhealth.integration.kafka.producer.impl

import dtu.openhealth.integration.kafka.producer.KafkaProducerService
import dtu.openhealth.integration.shared.dto.OmhDTO
import dtu.openhealth.integration.shared.util.PropertiesLoader
import io.vertx.core.logging.LoggerFactory
import io.vertx.reactivex.kafka.client.producer.KafkaProducer
import io.vertx.reactivex.kafka.client.producer.KafkaProducerRecord
import io.vertx.reactivex.core.Vertx
import java.util.HashMap

class KafkaProducerServiceImpl(vertx: Vertx) : KafkaProducerService {

    private val logger = LoggerFactory.getLogger(KafkaProducerServiceImpl::class.java)
    private val configuration = PropertiesLoader.loadProperties()

    private var producer: KafkaProducer<String, OmhDTO>
    init {
        val config: MutableMap<String, String> = HashMap()
        config["bootstrap.servers"] = configuration.getProperty("kafka.bootstrap.servers")
        config["key.serializer"] = configuration.getProperty("kafka.string.serializer")
        config["value.serializer"] = configuration.getProperty("kafka.omh.serializer")
        config["acks"] = configuration.getProperty("kafka.acks")
        producer = KafkaProducer.create(vertx, config)
    }
    override fun sendOmhData(omhDTO: OmhDTO) {
        logger.info("Send data to Kafka stream: $omhDTO")
        producer.send(KafkaProducerRecord.create(configuration.getProperty("kafka.topic"), omhDTO))
    }
}
