package dtu.openhealth.integration.kafka.producer.impl

import dtu.openhealth.integration.kafka.producer.IKafkaProducerService
import dtu.openhealth.integration.shared.dto.OmhDTO
import dtu.openhealth.integration.shared.util.ConfigVault
import dtu.openhealth.integration.shared.util.PropertiesLoader
import io.vertx.core.logging.LoggerFactory
import io.vertx.reactivex.kafka.client.producer.KafkaProducer
import io.vertx.reactivex.kafka.client.producer.KafkaProducerRecord
import io.vertx.reactivex.core.Vertx
import io.vertx.reactivex.kafka.client.consumer.KafkaConsumer
import java.util.HashMap

class KafkaProducerServiceImpl(vertx: Vertx) : IKafkaProducerService {

    private val logger = LoggerFactory.getLogger(KafkaProducerServiceImpl::class.java)
    //private val configuration = PropertiesLoader.loadProperties()

    private var topic = ""
    private var producer: KafkaProducer<String, OmhDTO>? = null

    init {
        ConfigVault().getConfigRetriever(vertx).getConfig { ar ->
            if(ar.succeeded()){
                logger.info("Configuration retrieved from the vault")
                val config = ar.result()
                val kafkaConfig: MutableMap<String, String> = HashMap()
                kafkaConfig["bootstrap.servers"] = config.getString("kafka.bootstrap.servers")
                kafkaConfig["key.serializer"] = config.getString("kafka.string.serializer")
                kafkaConfig["value.serializer"] = config.getString("kafka.omh.serializer")
                kafkaConfig["acks"] = config.getString("kafka.acks")
                topic = config.getString("kafka.topic")
                producer = KafkaProducer.create(vertx, kafkaConfig)
            }else{
                logger.error(ar.cause())
            }
        }
    }

    override fun sendOmhData(omhDTO: OmhDTO) {
        logger.info("Send data to Kafka topic($topic): $omhDTO")
        producer?.send(KafkaProducerRecord.create(topic, omhDTO))
    }
}
